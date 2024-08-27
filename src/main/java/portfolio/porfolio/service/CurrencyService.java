package portfolio.porfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final WebClient.Builder webClientBuilder;
    private final String EXCHANGE_RATE_API_URL = "https://api.exchangerate-api.com/v4/latest/USD"; // This URL provides rates for USD as the base currency.

    public Mono<Double> getExchangeRateToCurrency(String currencyCode) {
        return webClientBuilder.build()
                .get()
                .uri(EXCHANGE_RATE_API_URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.path("rates").path(currencyCode).asDouble());
    }

}
