package com.microsoft.openai.samples.insurancedemo.model;

public class InsuranceResponse {
    private String replyText;
    private ExpenseType expenseType;
    private String numberOfPeople;
    private String merchantName;
    private String geographicalLocation;
    private String businessJustification;

    public InsuranceResponse(String replyText, ExpenseType expenseType, String merchantName, String numberOfPeople, String geographicalLocation, String businessJustification) {
        this.replyText = replyText;
        this.expenseType = expenseType;
        this.numberOfPeople = numberOfPeople;
        this.merchantName = merchantName;
        this.geographicalLocation = geographicalLocation;
        this.businessJustification = businessJustification;

    }
 

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String carBrand) {
        this.merchantName = carBrand;
    }

    public String getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(String numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getGeographicalLocation() {
        return geographicalLocation;
    }

    public void setGeographicalLocation(String geographicalLocation) {
        this.geographicalLocation = geographicalLocation;
    }

    public String getBusinessJustification() {
        return businessJustification;
    }

    public void setBusinessJustification(String businessJustification) {
        this.businessJustification = businessJustification;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }
}