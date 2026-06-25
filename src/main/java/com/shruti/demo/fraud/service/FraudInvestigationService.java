package com.shruti.demo.fraud.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shruti.demo.fraud.model.FraudInvestigationReport;
import com.shruti.demo.fraud.model.TransactionInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class FraudInvestigationService {

    private static final Logger log = LoggerFactory.getLogger(FraudInvestigationService.class);

    private final ClaudeApiClient claudeApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:prompts/system-prompt.txt")
    private Resource systemPromptResource;

    @Value("classpath:prompts/user-prompt-template.txt")
    private Resource userPromptTemplateResource;

    public FraudInvestigationService(ClaudeApiClient claudeApiClient) {
        this.claudeApiClient = claudeApiClient;
    }

    @Cacheable(value = "fraudInvestigations", key = "#input.transactionAmount + '_' + #input.transactionVelocity + '_' + #input.geographicMatch + '_' + #input.cardType + '_' + #input.timeOfDay + '_' + #input.channel")
    public FraudInvestigationReport investigate(TransactionInput input) {
        log.info("Cache MISS -- calling Claude API for signals: {} | {} | {} | {} | {} | {}",
                input.getTransactionAmount(),
                input.getTransactionVelocity(),
                input.getGeographicMatch(),
                input.getCardType(),
                input.getTimeOfDay(),
                input.getChannel());

        try {
            String systemPrompt = systemPromptResource
                    .getContentAsString(StandardCharsets.UTF_8);

            String userMessage = userPromptTemplateResource
                    .getContentAsString(StandardCharsets.UTF_8)
                    .replace("{transactionAmount}", input.getTransactionAmount())
                    .replace("{transactionVelocity}", input.getTransactionVelocity())
                    .replace("{geographicMatch}", input.getGeographicMatch())
                    .replace("{cardType}", input.getCardType())
                    .replace("{timeOfDay}", input.getTimeOfDay())
                    .replace("{channel}", input.getChannel());

            ClaudeApiClient.ClaudeResponse claudeResponse =
                    claudeApiClient.complete(systemPrompt, userMessage);

            log.info("Received response from Claude, output tokens={}",
                    claudeResponse.outputTokens());

            return parseReport(claudeResponse);

        } catch (Exception e) {
            throw new RuntimeException("Fraud investigation failed: " + e.getMessage(), e);
        }
    }

    private FraudInvestigationReport parseReport(
            ClaudeApiClient.ClaudeResponse claudeResponse) {

        FraudInvestigationReport report = new FraudInvestigationReport();

        try {
            String cleanJson = claudeResponse.text().trim()
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();
            JsonNode node = objectMapper.readTree(cleanJson);

            report.setRiskScore(node.path("riskScore").asText("UNKNOWN"));
            report.setSignalsDetected(node.path("signalsDetected").asText());
            report.setReasoning(node.path("reasoning").asText());
            report.setRecommendedAction(node.path("recommendedAction").asText());
            report.setModelUsed(claudeResponse.modelUsed());
            report.setInputTokens(claudeResponse.inputTokens());
            report.setOutputTokens(claudeResponse.outputTokens());

            report.setRiskColor(switch (report.getRiskScore()) {
                case "HIGH"   -> "red";
                case "MEDIUM" -> "orange";
                case "LOW"    -> "green";
                default       -> "gray";
            });

        } catch (Exception e) {
            log.error("Failed to parse Claude response as JSON", e);
            report.setRiskScore("UNKNOWN");
            report.setRiskColor("gray");
            report.setSignalsDetected("Unable to parse investigation report.");
            report.setReasoning(claudeResponse.text());
            report.setRecommendedAction("Flag for Manual Review");
        }

        return report;
    }
}