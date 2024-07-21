package portfolio.porfolio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TopCryptoInfo {
    private String id;
    private String symbol;
    private String name;

    @JsonProperty("current_price")
    private double currentPrice;

    @JsonProperty("price_change_percentage_24h")
    private double priceChange24h;

    @JsonProperty("price_change_percentage_30d")
    private double priceChange30d;

    @JsonProperty("price_change_percentage_1y")
    private double priceChange1y;
}
