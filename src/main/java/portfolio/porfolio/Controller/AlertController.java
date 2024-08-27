package portfolio.porfolio.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import portfolio.porfolio.model.Alert;
import portfolio.porfolio.repository.AlertRepository;

import java.util.List;

@Controller
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;

    @GetMapping
    public String viewAlerts(Model model) {
        List<Alert> alerts = alertRepository.findAll();
        model.addAttribute("alerts", alerts);
        return "alerts";
    }

    @PostMapping("/add")
    public String addAlert(@ModelAttribute Alert alert) {
        alertRepository.save(alert);
        return "redirect:/alerts";
    }

    @PostMapping("/delete/{id}")
    public String deleteAlert(@PathVariable Long id) {
        alertRepository.deleteById(id);
        return "redirect:/alerts";
    }
}
