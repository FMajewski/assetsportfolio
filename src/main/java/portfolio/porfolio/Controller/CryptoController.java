package portfolio.porfolio.Controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import portfolio.porfolio.model.DetailedCryptoInfo;
import portfolio.porfolio.model.TopCryptoInfo;
import portfolio.porfolio.service.CryptoPriceService;
import portfolio.porfolio.service.CryptoService;
import portfolio.porfolio.model.Crypto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/crypto")
public class CryptoController {

    private final CryptoService cryptoService;
    private final CryptoPriceService cryptoPriceService;

    @PostMapping
    public Crypto addCrypto(@RequestBody Crypto crypto) {
        return cryptoService.addCrypto(crypto);
    }

    @GetMapping
    public List<Crypto> showAllAddedCrypto() {
        return cryptoService.showAllAddedCryptos();
    }

    @GetMapping("/summarized")
    public Mono<Map<String, DetailedCryptoInfo>> getSummarizedCryptos() {
        return cryptoService.getSummarizedCryptos();
    }

    @PostMapping("/prices")
    public Mono<Map<String, Double>> getCryptoPrices(@RequestBody List<String> symbols) {
        return cryptoPriceService.getCryptoPrices(symbols);

    }
    @GetMapping("/top")
    public Mono<List<TopCryptoInfo>> getTopCryptos() {
        return cryptoService.getTopCryptos();
    }
}
