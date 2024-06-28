package com.example.autobriefing.controller;

import com.example.autobriefing.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebCrawlerController {
    private final WebCrawlerService webCrawlerService;

    @Autowired
    public WebCrawlerController(WebCrawlerService webCrawlerService) {
        this.webCrawlerService = webCrawlerService;
    }

    @GetMapping("/crawl")
    public String crawl(@RequestParam String url) {
        webCrawlerService.crawl(url);
        return "crawling started for " + url;
    }
}
