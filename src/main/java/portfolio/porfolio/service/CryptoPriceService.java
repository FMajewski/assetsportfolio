package portfolio.porfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import portfolio.porfolio.mapper.CryptoPriceResponse;
import portfolio.porfolio.model.TopCryptoInfo;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoPriceService {

    private final WebClient.Builder webClientBuilder;


    @Value("${crypto.api.url}")
    private String apiUrl;


    @Value("${crypto.api.key}")
    private String apiKey;

    @Value("${crypto.api.top.url}")
    private String topUrl;

    public Mono<Map<String, Double>> getCryptoPrices(List<String> symbols) {
        String ids = String.join(",", symbols).toLowerCase();
        String url = String.format(apiUrl, ids);
        return webClientBuilder.build()
                .get()
                .uri(url)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(CryptoPriceResponse.class)
                .map(response -> symbols.stream()
                        .collect(Collectors.toMap(
                                symbol -> symbol,
                                symbol -> {
                                    Map<String, Double> priceMap = response.getPrices().get(symbol.toLowerCase());
                                    if (priceMap != null && priceMap.get("usd") != null) {
                                        return priceMap.get("usd");
                                    } else {
                                        throw new RuntimeException("Could not fetch price for symbol: " + symbol);
                                    }
                                }
                        )));
    }

    public Mono<List<TopCryptoInfo>> getTopCryptos() {
        return webClientBuilder.build()
                .get()
                .uri(topUrl)
                .retrieve()
                .bodyToFlux(TopCryptoInfo.class)
                .collectList();
    }


}
