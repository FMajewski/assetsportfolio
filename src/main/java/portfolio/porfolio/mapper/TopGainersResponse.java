package portfolio.porfolio.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import portfolio.porfolio.model.TopGainerInfo;

import java.util.List;

@Data
public class TopGainersResponse {
    @JsonProperty("top_gainers")
    private List<TopGainerInfo> topGainers;
}