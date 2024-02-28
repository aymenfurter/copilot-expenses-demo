package com.microsoft.openai.samples.insurancedemo.model;

public class InsuranceResponse {
    private String replyText;
    private ExpenseType expenseType;
    private String numberOfPeople;
    private String merchantName;
    private String geographicalLocation;
    private String numberOfPeopleReasoning;
    private String total;

    public InsuranceResponse(String replyText, ExpenseType expenseType, String merchantName, String numberOfPeople, String geographicalLocation, String numberOfPeopleReasoning, String total) {
        this.replyText = replyText;
        this.expenseType = expenseType;
        this.numberOfPeople = numberOfPeople;
        this.merchantName = merchantName;
        this.geographicalLocation = geographicalLocation;
        this.numberOfPeopleReasoning = numberOfPeopleReasoning;
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public String getNumberOfPeopleReasoning() {
        return numberOfPeopleReasoning;
    }

    public void setNumberOfPeopleReasoning(String numberOfPeopleReasoning) {
        this.numberOfPeopleReasoning = numberOfPeopleReasoning;
    }
}