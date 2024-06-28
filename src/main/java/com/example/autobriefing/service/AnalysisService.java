package com.example.autobriefing.service;

import com.example.autobriefing.model.Candidate;
import com.example.autobriefing.repository.AnalysisRepository;
import com.example.autobriefing.repository.CandidateRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j
@Service
public class AnalysisService {


    private final AnalysisRepository analysisRepository;
    private final CandidateRepository candidateRepository;
    private final OpenAiChatModel chatModel;

    @Autowired
    public AnalysisService(OpenAiChatModel chatModel, AnalysisRepository analysisRepository, CandidateRepository candidateRepository) {
        this.analysisRepository = analysisRepository;
        this.candidateRepository = candidateRepository;
        this.chatModel = chatModel;
    }

    public void generateAnalysis() {
        UserMessage userMessage = new UserMessage("What's the weather like in San Francisco, Tokyo, and Paris?");
        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder().withFunction("CurrentWeather").build()));

        log.info("Response: {}", response);
    }

    public String generateNewsAnalysis() {
        UserMessage userMessage = new UserMessage("From the recent news, select 4 news you think is the most important on understanding the cryptocurrency market trend and return them in a json format with a key of mostImportantNews, each object with keys id, title, contents");
        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder().withFunction("RecentNews").withResponseFormat(new ChatCompletionRequest.ResponseFormat("json_object")).build()));

        log.info("Response: {}", response);

        return response.getResult().getOutput().getContent();
    }

    public void saveResponseToCandidates(String responseString) {
        // Parse the response JSON
        JSONObject jsonResponse = new JSONObject(responseString);
        JSONArray newsArray = jsonResponse.getJSONArray("mostImportantNews");


        for (int i = 0; i < newsArray.length(); i++) {
            JSONObject newsItem = newsArray.getJSONObject(i);

            Candidate candidate = new Candidate();
            candidate.setId(newsItem.getLong("id"));
            candidate.setTitle(newsItem.getString("title"));
            candidate.setContents(newsItem.getString("contents"));

            candidateRepository.save(candidate);
        }
    }

    public void generateBitcoinAnalysis() {
        UserMessage userMessage = new UserMessage("Give me your price analysis of the bitcoin from the two datasets. One with interval : '1d', limit 7 (one week's data), other with interval : '1h', limit: 24 (one day's data)");
        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder().withFunction("GetBitcoinPrice").build()));
        log.info("Response: {}", response);
    }

}
