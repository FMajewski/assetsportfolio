package portfolio.porfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import portfolio.porfolio.model.Alert;
import portfolio.porfolio.model.Crypto;
import portfolio.porfolio.repository.AlertRepository;
import portfolio.porfolio.repository.CryptoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final CryptoRepository cryptoRepository;
    private final AlertRepository alertRepository;
    private final EmailService emailService;

    @Async
    public void checkAlerts() {
        List<Alert> alerts = alertRepository.findAll();

        for (Alert alert : alerts) {
            Crypto crypto = cryptoRepository.findBySymbol(alert.getCryptoSymbol())
                    .orElseThrow(() -> new IllegalArgumentException("Crypto not found for symbol: " + alert.getCryptoSymbol()));


            if (crypto != null && crypto.getPrice() >= alert.getTargetPrice()) {
                System.out.println("Alert! Kryptowaluta " + alert.getCryptoSymbol() + " osiągnęła cenę " + alert.getTargetPrice());


                String subject = "Your crypto price alert!";
                String content = "Ypur crypto price has reached your target price";
                emailService.sendEmail("filiptest413@gmail.com",subject, content);



                alertRepository.delete(alert);
            }
        }
    }

}
