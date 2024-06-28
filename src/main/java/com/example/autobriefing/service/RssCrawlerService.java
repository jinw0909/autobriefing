package com.example.autobriefing.service;

import com.example.autobriefing.model.Article;
import com.example.autobriefing.repository.ArticleRepository;
import com.rometools.modules.mediarss.MediaModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.modules.mediarss.types.Thumbnail;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rometools.modules.mediarss.MediaEntryModule;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RssCrawlerService {

    private final ArticleRepository articleRepository;

    @Autowired
    public RssCrawlerService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
    public List<SyndEntry> fetchRssFeed(String rssUrl) {
        List<SyndEntry> entries = new ArrayList<>();
        try {
            // Fetch the RSS feed using Jsoup
            URL url = new URL(rssUrl);
            try (XmlReader reader = new XmlReader(url)) {
                SyndFeed feed = new SyndFeedInput().build(reader);
                entries.addAll(feed.getEntries());
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error fetching RSS feed: " + rssUrl);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        printFeedDetails(entries);
        return entries;
    }

    public void processFeed(String rssUrl) {
        List<SyndEntry> entries = fetchRssFeed(rssUrl);
        entries.forEach(this::saveArticleData);
    }

    private void printFeedDetails(List<SyndEntry> entries) {
        for (SyndEntry entry : entries) {
            System.out.println("Title: " + entry.getTitle());
            System.out.println("Link: " + entry.getLink());
            Date pubDate = entry.getPublishedDate();
            System.out.println("Published Date: " + (pubDate != null ? pubDate : "No Publish Date"));
            System.out.println("-----------------------------------------");
        }
    }

    private String extractThumbnailURL(SyndEntry entry) {
        MediaEntryModule mediaModule = (MediaEntryModule) entry.getModule(MediaModule.URI);
        if (mediaModule != null) {
            Thumbnail[] thumbnails = mediaModule.getMetadata().getThumbnail();
            if (thumbnails != null && thumbnails.length > 0) {
                return thumbnails[0].getUrl().toString(); // Return the URL of the first thumbnail
            }
        }
        return "No Image Available"; // Default when no thumbnail is found
    }
    private String extractIdFromLink(String link) {
        return link.substring(link.lastIndexOf('/') + 1);
    }
    private String fetchContentFromUrl(String url) {
        try {
            log.info("Connecting to URL: {}", url);
            Document doc = Jsoup.connect(url).get();
            log.info("Fetched document successfully");
            Elements contents = doc.select("#pavo_contents"); // Select the content inside #pavo-contents
            if (contents.isEmpty()) {
                log.warn("No elements found with #pavo_contents selector");
                return "No content available";
            } else {
                // Remove or ignore text from <a> and <blockquote> tags
                contents.select("iframe, img").remove();

                // Extracting text after removing specific elements
                String textContent = contents.text().trim().replaceAll("\\n", "");
                // Finding the index of a specific phrase
                String specificPhrase = "속보는 블록미디어 텔레그램으로";
                int index = textContent.indexOf(specificPhrase);

                // Slicing the text up to the specific phrase, if it exists
                String finalContent = index != -1 ? textContent.substring(0, index) : textContent;
                log.info("Extracted content length: {} characters", finalContent.length());

                return finalContent;
            }
        } catch (IOException e) {
            System.err.println("Error fetching content from URL: " + url);
            e.printStackTrace();
            return "Failed to fetch content";
        }
    }

    private void saveArticleData(SyndEntry entry) {

        Article article = new Article(
            extractIdFromLink(entry.getLink()),
            entry.getTitle(),
            fetchContentFromUrl(entry.getLink()),
            extractThumbnailURL(entry),
            entry.getPublishedDate()
        );

        articleRepository.save(article);
        log.info("Article saved with ID: {}", article.getId());
    }



}
