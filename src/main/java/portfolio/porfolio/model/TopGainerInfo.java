package portfolio.porfolio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TopGainerInfo {

    @JsonProperty("ticker")
    private String symbol;

    @JsonProperty("price")
    private double currentPrice;

    @JsonProperty("change_amount")
    private double changeAmount;

    @JsonProperty("change_percentage")
    private String changePercentage;  // Keeping it as String to handle percentage formatting

    @JsonProperty("volume")
    private long volume;
}