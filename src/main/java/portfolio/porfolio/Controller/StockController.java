package portfolio.porfolio.Controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import portfolio.porfolio.model.TopGainerInfo;
import portfolio.porfolio.service.StockService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("top24")
    public Mono<List<TopGainerInfo>> getTopGainersFromStockMarket(){
        return stockService.getTopGainers();
    }
}
