package portfolio.porfolio.Controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import portfolio.porfolio.model.Crypto;
import portfolio.porfolio.model.TopCryptoInfo;
import portfolio.porfolio.service.AnalysisService;
import portfolio.porfolio.service.CryptoService;

import java.util.List;

@Controller
@AllArgsConstructor

@RequestMapping("/portfolio")
public class HomeController {

    private final CryptoService cryptoService;
    private final AnalysisService analysisService;



    @GetMapping
    public String viewPortfolio(@RequestParam(defaultValue = "USD") String currency, Model model) {
        cryptoService.updateCryptoPricesInDb();

        List<Crypto> cryptos = cryptoService.getCryptosInCurrency(currency).block();
        model.addAttribute("cryptos", cryptos);
        model.addAttribute("currency", currency);

        Double totalValue = cryptoService.calculateTotalMarketValueInCurrency(currency).block();
        model.addAttribute("totalValue", totalValue);

        List<TopCryptoInfo> topCryptos = cryptoService.getTopCryptos().block();
        model.addAttribute("topCryptos", topCryptos);

        String bitcoinAnalysis = analysisService.getBitcoinAnalysis().block();
        model.addAttribute("bitcoinAnalysis", bitcoinAnalysis);

        return "home";
    }



    @GetMapping("/add")
    public String showAddCryptoForm(Model model) {
        model.addAttribute("crypto", new Crypto());
        return "addCrypto";
    }

    @PostMapping("/add")
    public String addCrypto(@ModelAttribute Crypto crypto) {
        cryptoService.addCrypto(crypto);
        return "redirect:/portfolio";
    }

    @GetMapping("/delete")
    public String showDeleteCryptoForm(Model model) {
        model.addAttribute("crypto", new Crypto());
        return "deleteCrypto";
    }

    @PostMapping("/delete")
    public String deleteCrypto(@ModelAttribute Crypto crypto) {
        cryptoService.deleteCrypto(crypto);
        return "redirect:/portfolio";
    }
}
