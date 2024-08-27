package portfolio.porfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final WebClient.Builder webClientBuilder;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Cacheable(value = "btcAnalysis")
    public Mono<String> getBitcoinAnalysis() {
        return getBitcoinChartData() // Fetch the Bitcoin chart data first
                .flatMap(chartData -> // Once data is available, pass it to the next step
                        webClientBuilder.build()
                                .post()
                                .uri("https://api.openai.com/v1/chat/completions")
                                .header("Authorization", "Bearer " + openaiApiKey)
                                .header("Content-Type", "application/json")
                                .bodyValue(Map.of(
                                        "model", "gpt-3.5-turbo",
                                        "messages", List.of(
                                                Map.of("role", "system", "content", "You are a helpful assistant."),
                                                Map.of("role", "user", "content", "Based on the Bitcoin chart data from the last 7 days, provide a detailed technical analysis, focusing on key trends, moving averages, support/resistance levels, and any potential breakout signals. Also, discuss the market sentiment and possible future price movements based on the current data." + chartData)
                                        ),
                                        "max_tokens", 500,
                                        "temperature", 0.7
                                ))
                                .retrieve()
                                .bodyToMono(JsonNode.class) // Get the response as JSON
                                .map(response -> response
                                        .path("choices")
                                        .get(0)
                                        .path("message")
                                        .path("content")
                                        .asText() // Extract the content part of the response
                                )
                );
    }


    public Mono<String> getBitcoinChartData() {
        return webClientBuilder.build()
                .get()
                .uri("https://api.coingecko.com/api/v3/coins/bitcoin/market_chart?vs_currency=usd&days=7")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    List<Double> prices = new ArrayList<>();
                    response.path("prices").forEach(priceEntry -> {
                        prices.add(priceEntry.get(1).asDouble());
                    });

                    // Calculate Moving Averages (example for 3-day moving average)
                    List<Double> movingAverages = calculateMovingAverage(prices, 3);

                    StringBuilder analysisData = new StringBuilder();
                    for (int i = 0; i < prices.size(); i++) {
                        analysisData.append("Price: ").append(prices.get(i))
                                .append(", 3-Day Moving Average: ")
                                .append(i >= 2 ? movingAverages.get(i - 2) : "N/A")
                                .append("\n");
                    }
                    return analysisData.toString();
                });
    }

    private List<Double> calculateMovingAverage(List<Double> prices, int period) {
        List<Double> movingAverages = new ArrayList<>();
        for (int i = 0; i <= prices.size() - period; i++) {
            double sum = 0;
            for (int j = i; j < i + period; j++) {
                sum += prices.get(j);
            }
            movingAverages.add(sum / period);
        }
        return movingAverages;
    }

    @Scheduled(fixedRate = 30000000)
    @CacheEvict(value = "btcAnalysis", allEntries = true)
    public void evictBtcAnalysis() {
    }
}
