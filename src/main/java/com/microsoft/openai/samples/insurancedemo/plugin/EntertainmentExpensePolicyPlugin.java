package com.microsoft.openai.samples.insurancedemo.plugin;

import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;

public class EntertainmentExpensePolicyPlugin {
    private final String mockedResponse;

    public EntertainmentExpensePolicyPlugin() {
        this.mockedResponse = generateMockedResponse();
    }

    private String generateMockedResponse() {
        return "{\n" +
            "  \"policyType\": \"Meal Expense Policy\",\n" +
            "  \"threshold\": \"30 CHF per person\",\n" +
            "  \"eligibilityCriteria\": {\n" +
            "    \"description\": \"This policy applies to all employees for expenses.\",\n" +
            "    \"eligibleEmployees\": \"All full-time and part-time employees\"\n" +
            "  }\n" + 
            "}";
    }

    @DefineSKFunction(description = "Fetch the expense policy", name = "GetExpensePolicy")
    public String getEntertainmentExpensePolicy() {
        return mockedResponse;
    }
}
