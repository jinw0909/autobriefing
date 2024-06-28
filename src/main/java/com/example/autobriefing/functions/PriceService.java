//package com.example.autobriefing.functions;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyDescription;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.List;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//public class PriceService implements Function<PriceService.Request, PriceService.Response> {
//    public record Request(@JsonProperty(required = true, value = "interval") @JsonPropertyDescription("interval hours for the bitcoin price data") String interval
//        , @JsonProperty(required = true, value = "limit") @JsonPropertyDescription("query limit for the bitcoin price data") int limit) {}
//    public record Response(List<List<String>> klines) {}
//
//    @Override
//    public Response apply(Request request) {
//        String symbol = "BTCUSDT";
//        List<List<String>> klines = fetchBitcoinPrice(request.interval(), request.limit());
//        return new Response(klines);
//    }
//
//    private List<List<String>> fetchBitcoinPrice(String interval, int limit) {
//        try {
//            String apiURL = String.format("https://api.binance.com/api/v3/klines?symbol=BTCUSDT&interval=%s&limit=%d", interval, limit);
//            URL url = new URL(apiURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode != 200) {
//                throw new RuntimeException("HttpResponseCode: " + responseCode);
//            } else {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String response = reader.lines().collect(Collectors.joining());
//                // Extract the closing price of the most recent kline
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode jsonNode = mapper.readTree(response);
//                return StreamSupport.stream(jsonNode.spliterator(), false)
//                        .map(klineNode -> StreamSupport.stream(klineNode.spliterator(), false)
//                                .map(JsonNode::asText)
//                                .collect(Collectors.toList()))
//                        .collect(Collectors.toList());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
package com.example.autobriefing.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PriceService implements Function<PriceService.Request, PriceService.Response> {

    public record Request(@JsonProperty(required = true, value = "currentTime") @JsonPropertyDescription("Current time in KST") String time,
                          @JsonProperty(required = true, value = "interval") @JsonPropertyDescription("The interval for the klines data") String interval,
                          @JsonProperty(required = true, value = "limit") @JsonPropertyDescription("The limit for the klines data") int limit) {}

    public record Kline(@JsonProperty("openTime") String openTime,
                        @JsonProperty("open") String open,
                        @JsonProperty("high") String high,
                        @JsonProperty("low") String low,
                        @JsonProperty("close") String close,
                        @JsonProperty("volume") String volume,
                        @JsonProperty("closeTime") String closeTime,
                        @JsonProperty("quoteAssetVolume") String quoteAssetVolume,
                        @JsonProperty("numberOfTrades") String numberOfTrades,
                        @JsonProperty("takerBuyBaseAssetVolume") String takerBuyBaseAssetVolume,
                        @JsonProperty("takerBuyQuoteAssetVolume") String takerBuyQuoteAssetVolume,
                        @JsonProperty("ignore") String ignore) {}

    public record Response(@JsonProperty("klines") List<Kline> klines) {}

    @Override
    public Response apply(Request request) {
        List<Kline> klines = fetchBitcoinKlines(request.interval(), request.limit());
        return new Response(klines);
    }

    private List<Kline> fetchBitcoinKlines(String interval, int limit) {
        String apiUrl = String.format("https://api.binance.com/api/v3/klines?symbol=BTCUSDT&interval=%s&limit=%d", interval, limit);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());
                reader.close();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response);

                return StreamSupport.stream(jsonNode.spliterator(), false)
                        .map(klineNode -> new Kline(
                                klineNode.get(0).asText(),
                                klineNode.get(1).asText(),
                                klineNode.get(2).asText(),
                                klineNode.get(3).asText(),
                                klineNode.get(4).asText(),
                                klineNode.get(5).asText(),
                                klineNode.get(6).asText(),
                                klineNode.get(7).asText(),
                                klineNode.get(8).asText(),
                                klineNode.get(9).asText(),
                                klineNode.get(10).asText(),
                                klineNode.get(11).asText()))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
