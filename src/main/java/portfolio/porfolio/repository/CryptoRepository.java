package portfolio.porfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.porfolio.model.Crypto;

public interface CryptoRepository extends JpaRepository<Crypto, Long> {
}
