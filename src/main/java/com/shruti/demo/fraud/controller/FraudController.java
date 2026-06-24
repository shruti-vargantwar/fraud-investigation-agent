package com.shruti.demo.fraud.controller;

import com.shruti.demo.fraud.model.FraudInvestigationReport;
import com.shruti.demo.fraud.model.TransactionInput;
import com.shruti.demo.fraud.service.FraudInvestigationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FraudController {

    private final FraudInvestigationService fraudInvestigationService;

    public FraudController(FraudInvestigationService fraudInvestigationService) {
        this.fraudInvestigationService = fraudInvestigationService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("transaction", new TransactionInput());
        return "index";
    }

    @PostMapping("/investigate")
    public String investigate(
            @ModelAttribute TransactionInput transaction,
            Model model) {

        FraudInvestigationReport report =
                fraudInvestigationService.investigate(transaction);

        model.addAttribute("report", report);
        model.addAttribute("transaction", transaction);
        return "report";
    }
}