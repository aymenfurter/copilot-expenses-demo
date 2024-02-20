package com.microsoft.openai.samples.insurancedemo.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.openai.samples.insurancedemo.client.OpenAIVisionClient;
import com.microsoft.openai.samples.insurancedemo.model.*;
import com.microsoft.openai.samples.insurancedemo.plugin.EntertainmentExpensePolicyPlugin;
import com.microsoft.openai.samples.insurancedemo.plugin.MathPlugin;
import com.microsoft.openai.samples.insurancedemo.util.FileUtil;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.planner.actionplanner.ActionPlanner;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlanner;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlannerRequestSettings;
import com.microsoft.semantickernel.planner.stepwiseplanner.DefaultStepwisePlanner;
import com.microsoft.semantickernel.planner.stepwiseplanner.StepwisePlanner;
import com.microsoft.semantickernel.textcompletion.TextCompletion;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.GenericImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Set;


@Service
public class ImageAnalysisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAnalysisService.class);

    @Autowired
    private OpenAIVisionClient openAIVisionClient;

    public InsuranceResponse analyzeImage(MultipartFile file) {
        try {
            VisionResponse response = openAIVisionClient.sendVisionRequest(FileUtil.convertMultiPartToFileBase64(file));
            return parseResponse(response.getResponseText());
        } catch (IOException e) {
            LOGGER.error("Error processing image: {}", e.getMessage());
            throw new RuntimeException("Error processing image upload");
        }
    }
    private static final String prompt = "1. What kind of expense type is this? (CLIENT_ENTERTAINMENT, GROUND_TRANSPORTATION, OTHER)" +
    "2. How many people have been eating?" +
    "3. Who is the merchant?" +
    "4. What location?" +
    "5. Provide a draft for a business justification. Add '...' at the end to indicate where the user should complete the text.";


    public InsuranceResponse parseResponse(String responseText) {
        if (!responseText.matches("(?s)1\\. .*2\\. .*3\\. .*4\\. .*5\\..*")) {
            throw new RuntimeException("Invalid response format");
        }

        String[] parts = responseText.split("\n");

        ExpenseType expenseType = ExpenseType.valueOf(parts[0].split("\\.")[1].trim());
        String numberOfPeople = parts[1].split("\\.")[1].trim();
        String merchantName = parts[2].split("\\.")[1].trim();
        String geographicalLocation = parts[3].split("\\.")[1].trim();
        String businessJustification = parts[4].substring(3).trim();

        return new InsuranceResponse(
            replyText(responseText),
            expenseType,
            merchantName,
            numberOfPeople,
            geographicalLocation,
            businessJustification
        );
    }

    public String replyText(String responseText) {
        String ENDPOINT = System.getenv("AZURE_OPEN_AI_ENDPOINT");
        String API_KEY = System.getenv("AZURE_OPEN_AI_KEY");

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .endpoint(ENDPOINT)
                .credential(new AzureKeyCredential(API_KEY))
                .buildAsyncClient();

        TextCompletion chatCompletion = SKBuilders.chatCompletion()
                .withOpenAIClient(client)
                .withModelId("gpt-4-32k")
                .build();

        Kernel kernel = SKBuilders.kernel()
                .withDefaultAIService(chatCompletion)
                .build();

        kernel.importSkill(new EntertainmentExpensePolicyPlugin(), "EntertainmentExpensePolicyPlugin");
        kernel.importSkill(new MathPlugin(), "MathPlugin");

        var goal = "Your goal is by using the ExpensesPolicyPlugin to 1. find out which if the filed expense is compliant with the expense policy. Provide the information about the related parts of the expense policy.Use an emoji in your response. \nExpense Claim Information: "
                + responseText;
        StepwisePlanner planner = new DefaultStepwisePlanner(kernel, null, null); 
        var plan = planner.createPlan(goal);
        SKContext result = plan.invokeAsync().block();
        return result.getResult();
    }
}
