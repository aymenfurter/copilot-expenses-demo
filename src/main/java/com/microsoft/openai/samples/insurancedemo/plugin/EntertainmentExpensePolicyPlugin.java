package com.microsoft.openai.samples.insurancedemo.plugin;

import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;

public class EntertainmentExpensePolicyPlugin {
    private final String mockedResponse;

    public EntertainmentExpensePolicyPlugin() {
        this.mockedResponse = generateMockedResponse();
    }

    private String generateMockedResponse() {
        return "{\n" +
            "  \"policyType\": \"Entertainment Expense Policy\",\n" +
            "  \"threshold\": \"30 CHF per person\",\n" +
            "  \"eligibilityCriteria\": {\n" +
            "    \"description\": \"This policy applies to all employees for expenses related to entertainment activities directly associated with the promotion of business goals.\",\n" +
            "    \"eligibleEmployees\": \"All full-time and part-time employees\",\n" +
            "    \"eligibleActivities\": [\"Business dinners\", \"Team-building events\", \"Client entertainment\"]\n" +
            "  }\n" + 
            "}";
    }

    @DefineSKFunction(description = "Fetch a mocked Entertainment Expense Policy", name = "GetEntertainmentExpensePolicy")
    public String getEntertainmentExpensePolicy() {
        return mockedResponse;
    }
}
