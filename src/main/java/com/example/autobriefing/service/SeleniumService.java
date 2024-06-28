package com.example.autobriefing.service;

import com.example.autobriefing.model.News;
import com.example.autobriefing.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.processing.Find;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class SeleniumService {

    private final NewsRepository newsRepository;

    public SeleniumService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public void performWebScraping() {

        try {
            File chromeDriverFile = new ClassPathResource("chromedriver-mac-arm64/chromedriver").getFile();
            System.setProperty("webdriver.chrome.driver", chromeDriverFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to set ChromeDriver path", e);
            return;
        }

        // Configure ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1200");

        WebDriver driver = new ChromeDriver(options);

        try {
            // Navigate to a website
            driver.get("https://coinness.com");
            log.info("Navigated to https://coinness.com");

            // Get the title of the page
            String pageTitle = driver.getTitle();
            log.info("Page Title: {}", pageTitle);

            // Use WebDriverWait to wait for the button to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Click the button 5 times
            for (int i = 0; i < 3; i++) {
                WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='더보기']")));
                button.click();
                log.info("Clicked the button with text '더보기' {} time(s)", (i + 1));
            }

            // Locate all elements with class names starting with "BreakingNewsWrap"
            List<WebElement> newsWraps = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div[class^='BreakingNewsWrap']")));
            log.info("how many? : {}", newsWraps.size());

            // Check if the TimeBlock exceeds that of the last stored row
            News lastNews = newsRepository.findFirstByOrderByTimeBlockDesc();

            for (WebElement newsWrap : newsWraps) {
                // Find and log the TimeBlock element text
                WebElement timeBlock = newsWrap.findElement(By.cssSelector("div[class^='TimeBlock']"));
                String timeBlockText = timeBlock.getText();
                log.info("TimeBlock: {}", timeBlockText);

                // Find and log the BreakingNewsTitle element text
                WebElement newsTitle = newsWrap.findElement(By.cssSelector("div[class^='BreakingNewsTitle']"));
                String newsTitleText = newsTitle.getText();
                log.info("BreakingNewsTitle: {}", newsTitleText);

                // Find and log the BreakingNewsContents element text
                WebElement newsContents = newsWrap.findElement(By.cssSelector("div[class^='BreakingNewsContents']"));
                String newsContentsText = newsContents.getText();
                log.info("BreakingNewsContents: {}", newsContentsText);

                // Convert TimeBlock to KST LocalDateTime
                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                LocalTime time = LocalTime.parse(timeBlockText, DateTimeFormatter.ofPattern("HH:mm"));
                LocalDateTime timeBlockDateTime = LocalDateTime.of(today, time);

                if (lastNews == null || timeBlockDateTime.isAfter(lastNews.getTimeBlock())) {
                    News news  = new News();
                    news.setTimeBlock(timeBlockDateTime);
                    news.setTitle(newsTitleText);
                    news.setContents(newsContentsText);
                    newsRepository.save(news);
                    log.info("Saved news entity: {}", news);
                } else {
                    log.info("TimeBlock does not exceed the last stored row. Entity not saved. {}", timeBlockDateTime);
                }

            }

        } catch (Exception e) {
            log.error("An error occurred during web scraping", e);
        } finally {
            driver.quit();
            log.info("Driver quit successfully");
        }
    }

    public void performUrlScraping(String url) {
        try {
            File chromeDriverFile = new ClassPathResource("chromedriver-mac-arm64/chromedriver").getFile();
            System.setProperty("webdriver.chrome.driver", chromeDriverFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to set ChromeDriver path", e);
            return;
        }

        // Configure ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(url);
            log.info("Navigated to {}", url);
        } catch (Exception e) {
            log.error("An error occurred during web scraping", e);
        }
    }

    public List<News> getRecentNews() {
        // Calculate the time 24 hours ago in KST
        LocalDateTime nowKST = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime startTime = nowKST.minusHours(24);

        // Retrieve news from the last 24 hours
        return newsRepository.findAllNewsFromLast24Hours(startTime);
    }


}
