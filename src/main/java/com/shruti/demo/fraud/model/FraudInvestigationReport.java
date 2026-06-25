package com.shruti.demo.fraud.model;

public class FraudInvestigationReport implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String riskScore;
    private String riskColor;
    private String signalsDetected;
    private String reasoning;
    private String recommendedAction;
    private String modelUsed;
    private int inputTokens;
    private int outputTokens;

    public FraudInvestigationReport() {}

    public String getRiskScore() { return riskScore; }
    public void setRiskScore(String riskScore) { this.riskScore = riskScore; }

    public String getRiskColor() { return riskColor; }
    public void setRiskColor(String riskColor) { this.riskColor = riskColor; }

    public String getSignalsDetected() { return signalsDetected; }
    public void setSignalsDetected(String signalsDetected) { this.signalsDetected = signalsDetected; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public int getInputTokens() { return inputTokens; }
    public void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }

    public int getOutputTokens() { return outputTokens; }
    public void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }
}