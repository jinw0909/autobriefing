package com.example.autobriefing.config;

import com.example.autobriefing.functions.MockWeatherService;
import com.example.autobriefing.functions.NewsService;
import com.example.autobriefing.functions.PriceService;
import com.example.autobriefing.repository.NewsRepository;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    private final NewsRepository newsRepository;
    public Config(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Bean
    public FunctionCallback recentNewsFunctionInfo() {
        return FunctionCallbackWrapper.builder(new NewsService(newsRepository))
                .withName("RecentNews")
                .withDescription("returns the recent cryptocurrency articles published within 24 hours based on KST")
                .build();
    }

    @Bean
    public FunctionCallback weatherFunctionInfo() {
        return FunctionCallbackWrapper.builder(new MockWeatherService())
                .withName("CurrentWeather")
                .withDescription("Get the weather in location")
                .build();
    }

    @Bean
    public FunctionCallbackWrapper priceFunctionInfo() {
        return FunctionCallbackWrapper.builder(new PriceService())
                .withName("GetBitcoinPrice")
                .withDescription("Take interval and limit as parameters and returns the Bitcoin price data")
                .build();
    }
}
