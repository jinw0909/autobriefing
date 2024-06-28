package com.example.autobriefing.functions;

import com.example.autobriefing.model.News;
import com.example.autobriefing.repository.NewsRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;

public class NewsService implements Function<NewsService.Request, NewsService.Response> {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    public record Request(@JsonProperty(required = true, value = "currentTime") @JsonPropertyDescription("Current time in KST") String time) {}
    public record Response(List<News> newsList) {}
    public Response apply(Request request) {
        // Calculate the time 24 hours ago in KST
        LocalDateTime nowKST = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime startTime = nowKST.minusHours(24);
        // Retrieve news from the last 24 hours
        List<News> recentNews = newsRepository.findAllNewsFromLast24Hours(startTime);
        return new Response(recentNews);
    }
}
