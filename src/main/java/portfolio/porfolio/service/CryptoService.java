package portfolio.porfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import portfolio.porfolio.model.Crypto;
import portfolio.porfolio.model.TopCryptoInfo;
import portfolio.porfolio.repository.CryptoRepository;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoRepository cryptoRepository;
    private final CryptoPriceService cryptoPriceService;
    private final WebClient.Builder webClientBuilder;
    private final CurrencyService currencyService;
    private final AlertService alertService;


    @Value("${crypto.api.coins.url}")
    private String coinsUrl;

    @Value("${crypto.api.url}")
    private String apiUrl;

    @Value("${crypto.api.key}")
    private String apiKey;

    @Value("${crypto.api.top.url}")
    private String topUrl;


    @Transactional
    public Crypto addCrypto(Crypto crypto) {
        String symbol = crypto.getSymbol().toLowerCase();

        String formattedUrl = String.format(apiUrl, symbol);

        Map<String, Map<String, Double>> response = webClientBuilder.build()
                .get()
                .uri(formattedUrl)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                .block();

        if (response == null || !response.containsKey(symbol)) {
            throw new IllegalArgumentException("Invalid cryptocurrency: " + crypto.getSymbol());
        }

        Map<String, Double> priceData = response.get(symbol);
        Double price = priceData != null ? priceData.get("usd") : null;

        if (price == null) {
            throw new IllegalArgumentException("Could not retrieve price for: " + crypto.getSymbol());
        }
        crypto.setPrice(price);
        double marketValue = crypto.getAmount() * price;
        crypto.setMarketValue(marketValue);

        return cryptoRepository.save(crypto);
    }

    @Transactional
    public Crypto deleteCrypto(Crypto crypto) {
        double totalAmount = cryptoRepository.findAllBySymbol(crypto.getSymbol())
                .stream()
                .mapToDouble(Crypto::getAmount)
                .sum();

        if (totalAmount < crypto.getAmount()) {
            throw new IllegalArgumentException("The amount you want to delete is greater than what you have in your portfolio for " + crypto.getSymbol());
        }

        Crypto negativeTransaction = new Crypto();
        negativeTransaction.setSymbol(crypto.getSymbol());
        negativeTransaction.setAmount(-crypto.getAmount());
        negativeTransaction.setPrice(crypto.getPrice()); // or fetch the current price if needed
        negativeTransaction.setMarketValue(-crypto.getAmount() * crypto.getPrice());

        return cryptoRepository.save(negativeTransaction);
    }



    public List<Crypto> showAllAddedCryptos() {
        return cryptoRepository.findAll().stream()
                .collect(Collectors.groupingBy(Crypto::getSymbol, Collectors.reducing((crypto1, crypto2) -> {
                    crypto1.setAmount(crypto1.getAmount() + crypto2.getAmount());
                    crypto1.setMarketValue(crypto1.getMarketValue() + crypto2.getMarketValue());
                    return crypto1;
                })))
                .values().stream()
                .map(optionalCrypto -> optionalCrypto.orElseThrow(() -> new IllegalStateException("Unexpected empty Optional")))
                .collect(Collectors.toList());
    }


    @Async
    @Scheduled(fixedRate = 300000) // Co 5 minut
    public void updateCryptoPricesInDb() {
        List<Crypto> cryptos = cryptoRepository.findAll();

        List<String> symbols = cryptos.stream()
                .map(Crypto::getSymbol)
                .distinct()
                .collect(Collectors.toList());

        cryptoPriceService.getCryptoPrices(symbols)
                .subscribe(prices -> {
                    cryptos.forEach(crypto -> {
                        double price = prices.getOrDefault(crypto.getSymbol().toLowerCase(), 0.0);
                        double marketValue = crypto.getAmount() * price;
                        crypto.setPrice(price);
                        crypto.setMarketValue(marketValue);
                        cryptoRepository.save(crypto);
                    });
                });
        alertService.checkAlerts();
    }



    public Mono<Double> calculateTotalMarketValueInCurrency(String currencyCode) {
        return currencyService.getExchangeRateToCurrency(currencyCode)
                .map(exchangeRate -> cryptoRepository.findAll().stream()
                        .mapToDouble(crypto -> {
                            double priceInSelectedCurrency = crypto.getPrice();
                            if (!currencyCode.equalsIgnoreCase("USD")) {
                                priceInSelectedCurrency *= exchangeRate;
                            }
                            return crypto.getAmount() * priceInSelectedCurrency;
                        })
                        .sum());
    }


    @Cacheable(value = "topCryptosCache")
    public Mono<List<TopCryptoInfo>> getTopCryptos() {
        return webClientBuilder.build()
                .get()
                .uri(topUrl)
                .retrieve()
                .bodyToFlux(TopCryptoInfo.class)
                .collectList();
    }

    @Scheduled(fixedRate = 300000)
    @CacheEvict(value = "topCryptosCache", allEntries = true)
    public void evictTopCryptosCache() {
    }

    public Mono<List<Crypto>> getCryptosInCurrency(String currencyCode) {
        return currencyService.getExchangeRateToCurrency(currencyCode)
                .map(exchangeRate -> cryptoRepository.findAll().stream()
                        .peek(crypto -> {
                            // Only convert if the currency is not USD
                            if (!currencyCode.equalsIgnoreCase("USD")) {
                                double convertedPrice = crypto.getPrice() * exchangeRate;
                                crypto.setPrice(convertedPrice);
                                crypto.setMarketValue(crypto.getAmount() * convertedPrice);
                            }
                        })
                        .collect(Collectors.toList())
                );
    }


}
