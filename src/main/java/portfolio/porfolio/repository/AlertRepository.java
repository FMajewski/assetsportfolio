package portfolio.porfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.porfolio.model.Alert;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByCryptoSymbol(String cryptoSymbol);
}
