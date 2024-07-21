package portfolio.porfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import portfolio.porfolio.mapper.TopGainersResponse;
import portfolio.porfolio.model.TopGainerInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final WebClient.Builder webClientBuilder;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.api.url}")
    private String apiUrl;

    public Mono<List<TopGainerInfo>> getTopGainers() {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(apiUrl)
                        .path("/query")
                        .queryParam("function", "TOP_GAINERS_LOSERS")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(TopGainersResponse.class)
                .map(TopGainersResponse::getTopGainers);
    }
}
