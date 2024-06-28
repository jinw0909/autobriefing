package com.example.autobriefing.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class WebCrawlerService {

    private Set<String> visitedUrls = new HashSet<>();

    public void crawl(String url) {
        if (visitedUrls.contains(url)) {
            return;
        }
        visitedUrls.add(url);

        try {
            Document document = Jsoup.connect(url).get();
            System.out.println("Title: " + document.title());
            document.select("a[href]").forEach(link -> {
                String nextUrl = link.attr("abs:href");
                crawl(nextUrl);
            });
        } catch (IOException e) {
            System.err.println("Error fetching URL: " + url);
            e.printStackTrace();
        }
    }
}
