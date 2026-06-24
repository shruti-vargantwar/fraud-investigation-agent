package com.shruti.demo.fraud.model;

public class TransactionInput {

    private String transactionAmount;
    private String transactionVelocity;
    private String geographicMatch;
    private String cardType;
    private String timeOfDay;
    private String channel;

    public TransactionInput() {}

    public String getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(String transactionAmount) { this.transactionAmount = transactionAmount; }

    public String getTransactionVelocity() { return transactionVelocity; }
    public void setTransactionVelocity(String transactionVelocity) { this.transactionVelocity = transactionVelocity; }

    public String getGeographicMatch() { return geographicMatch; }
    public void setGeographicMatch(String geographicMatch) { this.geographicMatch = geographicMatch; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}