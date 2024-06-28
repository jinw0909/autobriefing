package com.example.autobriefing.service;

import com.example.autobriefing.model.Joke;
import com.example.autobriefing.repository.JokeRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JokeService {

    private final JokeRepository jokeRepository;
    private final OpenAiChatModel chatModel;

    @Autowired
    public JokeService(OpenAiChatModel chatModel, JokeRepository jokeRepository) {
        this.jokeRepository = jokeRepository;
        this.chatModel = chatModel;
    }

    public String generateJoke(String prompt) {
        return chatModel.call(prompt);
    }

    public Joke saveJoke(String content) {
        Joke joke = new Joke(content);
        return jokeRepository.save(joke);
    }
}
