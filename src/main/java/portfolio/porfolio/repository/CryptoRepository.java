package portfolio.porfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.porfolio.model.Crypto;

import java.util.List;
import java.util.Optional;

public interface CryptoRepository extends JpaRepository<Crypto, Long> {
    List<Crypto> findAllBySymbol(String symbol);

    Optional<Crypto> findBySymbol(String symbol);
}