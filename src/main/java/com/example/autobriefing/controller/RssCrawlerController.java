package com.example.autobriefing.controller;

import com.example.autobriefing.service.RssCrawlerService;
import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RssCrawlerController {

    private final RssCrawlerService rssCrawlerService;

    @Autowired
    public RssCrawlerController(RssCrawlerService rssCrawlerService) {
        this.rssCrawlerService = rssCrawlerService;
    }

    @GetMapping("/crawl-rss")
    public String crawlRss(@RequestParam(defaultValue = "https://www.blockmedia.co.kr/feed") String rssUrl) {
        rssCrawlerService.processFeed(rssUrl);
        return "OK";
    }
}
