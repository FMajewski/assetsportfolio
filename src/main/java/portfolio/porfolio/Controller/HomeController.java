package portfolio.porfolio.Controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import portfolio.porfolio.model.Crypto;
import portfolio.porfolio.model.DetailedCryptoInfo;
import portfolio.porfolio.model.TopCryptoInfo;
import portfolio.porfolio.model.TopGainerInfo;
import portfolio.porfolio.service.CryptoService;
import portfolio.porfolio.service.StockService;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/portfolio")
public class HomeController {

    private final CryptoService cryptoService;
    private final StockService stockService;

    @GetMapping
    public String viewPortfolio(Model model) {
        Map<String, DetailedCryptoInfo> cryptos = cryptoService.getSummarizedCryptos().block();
        model.addAttribute("cryptos", cryptos);

        List<TopCryptoInfo> topCryptos = cryptoService.getTopCryptos().block();
        model.addAttribute("topCryptos", topCryptos);

        List<TopGainerInfo> topGainers = stockService.getTopGainers().block();
        model.addAttribute("topGainers", topGainers);
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
}
