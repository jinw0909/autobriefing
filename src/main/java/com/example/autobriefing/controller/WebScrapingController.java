package com.example.autobriefing.controller;

import com.example.autobriefing.model.News;
import com.example.autobriefing.service.SeleniumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebScrapingController {

    private final SeleniumService seleniumService;

    @Autowired
    public WebScrapingController(SeleniumService seleniumService) {
        this.seleniumService = seleniumService;
    }

    @GetMapping("/scrape")
    public String scrapeWeb() {
        seleniumService.performWebScraping();
        return "Scraping completed";
    }

    @GetMapping("/scrape-url")
    public String scrapeUrl(@RequestParam String url) {
        seleniumService.performUrlScraping(url);
        return "Scraping completed";
    }

    @GetMapping("/recent")
    public List<News> recentNews() {
        List<News> recentNews = seleniumService.getRecentNews();
        return recentNews;
    }

}
