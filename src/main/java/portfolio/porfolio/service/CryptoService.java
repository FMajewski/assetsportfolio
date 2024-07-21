package portfolio.porfolio.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import portfolio.porfolio.model.Crypto;
import portfolio.porfolio.model.CryptoList;
import portfolio.porfolio.model.DetailedCryptoInfo;
import portfolio.porfolio.model.TopCryptoInfo;
import portfolio.porfolio.repository.CryptoRepository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoRepository cryptoRepository;
    private final CryptoPriceService cryptoPriceService;
    private final WebClient.Builder webClientBuilder;
    private Set<String> cryptoCurrencies;

    @Value("${crypto.api.coins.url}")
    private String coinsUrl;


    @Transactional
    public Crypto addCrypto(Crypto crypto) {
        if (!isValidCrypto(crypto.getName())) {
            throw new IllegalArgumentException("Invalid cryptocurrency: " + crypto.getSymbol());
        }
        return cryptoRepository.save(crypto);
    }

    public List<Crypto> showAllAddedCryptos() {
        return cryptoRepository.findAll();
    }

    public Mono<Map<String, DetailedCryptoInfo>> getSummarizedCryptos() {
        Map<String, Double> summedAmounts = cryptoRepository.findAll().stream()
                .collect(Collectors.groupingBy(Crypto::getName, Collectors.summingDouble(Crypto::getAmount)));

        List<String> symbols = summedAmounts.keySet().stream().collect(Collectors.toList());

        return cryptoPriceService.getCryptoPrices(symbols)
                .map(prices -> {
                    // Create a map of DetailedCryptoInfo
                    return summedAmounts.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> {
                                        String symbol = entry.getKey();
                                        double amount = entry.getValue();
                                        double price = prices.get(symbol.toLowerCase());
                                        DetailedCryptoInfo info = new DetailedCryptoInfo();
                                        info.setAmount(amount);
                                        info.setPrice(price);
                                        info.setMarketValue(amount * price);
                                        return info;
                                    }
                            ));
                });
    }

    @PostConstruct
    public void init() {
        fetchCryptoCurrencies();
    }

    public void fetchCryptoCurrencies() {
        List<CryptoList> response = webClientBuilder.build()
                .get()
                .uri(coinsUrl)
                .retrieve()
                .bodyToFlux(CryptoList.class)
                .collectList()
                .block();

        cryptoCurrencies = new HashSet<>();
        if (response != null) {
            response.forEach(crypto -> cryptoCurrencies.add(crypto.getName().toLowerCase()));
        }
    }

    public boolean isValidCrypto(String symbol) {
        return cryptoCurrencies.contains(symbol.toLowerCase());
    }

    public Mono<List<TopCryptoInfo>> getTopCryptos() {
        return cryptoPriceService.getTopCryptos();
    }

}
