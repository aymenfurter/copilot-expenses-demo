package com.microsoft.openai.samples.demo.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.openai.samples.demo.client.OpenAIVisionClient;
import com.microsoft.openai.samples.demo.model.*;
import com.microsoft.openai.samples.demo.plugin.EntertainmentExpensePolicyPlugin;
import com.microsoft.openai.samples.demo.plugin.MathPlugin;
import com.microsoft.openai.samples.demo.util.FileUtil;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.orchestration.SKFunction;
import com.microsoft.semantickernel.planner.actionplanner.ActionPlanner;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlanner;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlannerRequestSettings;
import com.microsoft.semantickernel.planner.stepwiseplanner.DefaultStepwisePlanner;
import com.microsoft.semantickernel.planner.stepwiseplanner.StepwisePlanner;
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import com.microsoft.semantickernel.orchestration.SKFunction;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig;

import reactor.core.publisher.Mono;

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

    public Response analyzeImage(MultipartFile file, String expenseType) {
        try {
            VisionResponse response = openAIVisionClient.sendVisionRequest(FileUtil.convertMultiPartToFileBase64(file));
            return parseResponse(response.getResponseText(), expenseType);
        } catch (IOException e) {
            LOGGER.error("Error processing image: {}", e.getMessage());
            throw new RuntimeException("Error processing image upload");
        }
    }

    public Response parseResponse(String responseText, String selectedExpenseType) {
        if (!responseText.matches("(?s)1\\. .*2\\. .*3\\. .*4\\. .*5\\. .*6\\..*")) {
            throw new RuntimeException("Invalid response format");
        }

        String[] parts = responseText.split("\n");

        ExpenseType expenseType = ExpenseType.valueOf(parts[0].split("\\.")[1].trim());
        String numberOfPeople = parts[1].split("\\.")[1].trim();
        String numberOfPeopleReasoning = parts[2].split("\\.")[1].trim();
        String merchantName = parts[3].split("\\.")[1].trim();
        String geographicalLocation = parts[4].split("\\.")[1].trim();
        String total = parts[5].split("\\.")[1].trim();

        return new Response(
            replyText(responseText, selectedExpenseType),
            expenseType,
            merchantName,
            numberOfPeople,
            geographicalLocation,
            numberOfPeopleReasoning,
            total
        );
    }

    public String replyText(String responseText, String selectedExpenseType) {
        String ENDPOINT = System.getenv("AZURE_OPEN_AI_ENDPOINT");
        String API_KEY = System.getenv("AZURE_OPEN_AI_KEY");

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .endpoint(ENDPOINT)
                .credential(new AzureKeyCredential(API_KEY))
                .buildAsyncClient();

        TextCompletion chatCompletion = SKBuilders.chatCompletion()
                .withOpenAIClient(client)
                .withModelId("gpt-35")
                .build();

        Kernel kernel = SKBuilders.kernel()
                .withDefaultAIService(chatCompletion)
                .build();

        kernel.importSkill(new EntertainmentExpensePolicyPlugin(), "EntertainmentExpensePolicyPlugin");
        kernel.importSkill(new MathPlugin(), "MathPlugin");

        var goal = "Your goal is to verify if the selected expense type ("+selectedExpenseType+") lines up with the extracted expense information (let the user know that they may have selected the wrong type if required). Then, by using the ExpensesPolicyPlugin to 1. find out if the filed expense is compliant with the expense policy. Provide the information about the related parts of the expense policy. Use an emoji in your response. \nExtracted Expense Claim Information: "
                + responseText;
        StepwisePlanner planner = new DefaultStepwisePlanner(kernel, null, null); 
        var plan = planner.createPlan(goal);
        SKContext result = plan.invokeAsync().block();

        String resultText = result.getResult();
        SKFunction rewrite = kernel
                .getSemanticFunctionBuilder()
                .withPromptTemplate("{{$input}} \n Rewrite the above analysis in a customer facing way. Format it using HTML tags, start with <div> tag (full HTML is not required, don't write ```html just start with <div>.. ). Use list items for the extracted information. Use a table for the expense policy information. Use an emoji in your response. Write the text semi-formal.")
                .withFunctionName("format")
                .build();

        Mono<SKContext> rewriteResult = rewrite.invokeAsync(resultText);

        return rewriteResult.block().getResult();
    }
}
