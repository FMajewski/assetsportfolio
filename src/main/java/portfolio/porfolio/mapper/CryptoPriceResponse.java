package portfolio.porfolio.mapper;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CryptoPriceResponse {

    private Map<String, Map<String, Double>> prices = new HashMap<>();

    @JsonAnySetter
    public void addPrice(String key, Map<String, Double> value) {
        prices.put(key, value);
    }
}