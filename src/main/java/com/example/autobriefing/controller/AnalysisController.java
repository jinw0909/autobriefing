package com.example.autobriefing.controller;

import com.example.autobriefing.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AnalysisController {

    private final AnalysisService analysisService;

    @Autowired
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/analysis")
    public void generate() {
        analysisService.generateAnalysis();
    }

    @GetMapping("/news-analysis")
    public String newsAnalysis() {
        String result = analysisService.generateNewsAnalysis();
        analysisService.saveResponseToCandidates(result);
        return "OK";
    }

    @GetMapping("/price-analysis")
    public void priceAnalysis() {
        analysisService.generateBitcoinAnalysis();
    }


}
