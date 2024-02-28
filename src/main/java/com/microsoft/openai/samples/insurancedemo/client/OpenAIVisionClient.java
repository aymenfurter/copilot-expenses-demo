package com.microsoft.openai.samples.insurancedemo.client;

import com.microsoft.openai.samples.insurancedemo.model.VisionResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

//FIXME: Move to SK/Azure OpenAI SDK once supported
@Component
public class OpenAIVisionClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIVisionClient.class);
    
    private String apiKey = System.getenv("AZURE_OPEN_AI_KEY");
    private static final String API_ENDPOINT = System.getenv("AZURE_OPEN_AI_ENDPOINT") + "openai/deployments/vision/chat/completions?api-version=2023-07-01-preview";
    private static final String prompt = "Answer these questions, only answer with the specific answer, dont form sentences. 1. What kind of expense type is this? (MEALS, TRAVEL, OFFICE_SUPPLIES, EVENTS, TRAINING, OTHER)" +
                            "2. How many people have been eating? (Only numbers, no text)" +
                            "3. How could you tell how many people were eating? (single sentence)" +
                            "4. Who is the merchant?" +
                            "5. What location?" +
                            "6. What is the total amount?";

    public VisionResponse sendVisionRequest(String base64Image) throws IOException {
        LOGGER.info("====================");
        LOGGER.info("Sending request to OpenAI Vision API");
        LOGGER.info("Query: {}", prompt);
        LOGGER.info("Image: {}", base64Image.substring(0, 30) + "....");
        LOGGER.info("====================");

        String payload = buildPayload(prompt, base64Image);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_ENDPOINT);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("api-key", apiKey);
            httpPost.setHeader("api-version", "2023-07-01-preview");
            httpPost.setEntity(new StringEntity(payload));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String output = EntityUtils.toString(response.getEntity());
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed: HTTP error code : " + response.getStatusLine().getStatusCode());
                }

                LOGGER.info("====================");
                LOGGER.info("Response from OpenAI Vision API");
                LOGGER.info("Response: {}", output);
                LOGGER.info("====================");
                return new VisionResponse(extractResponse(output));
            }
        }
    }

    private String extractResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject firstChoice = jsonObject.getJSONArray("choices").getJSONObject(0);
        String content = firstChoice.getJSONObject("message").getString("content");
        return content;
    }

    private String buildPayload(String query, String base64Image) {
        return "{"
                + "\"messages\": ["
                + "    {"
                + "        \"role\": \"user\","
                + "        \"content\": ["
                + "            {"
                + "                \"type\": \"text\","
                + "                \"text\": \"" + query + "\""
                + "            },"
                + "            {"
                + "                \"type\": \"image_url\","
                + "                \"image_url\": {"
                + "                    \"url\":\"" + base64Image + "\""
                + "                }"
                + "            }"
                + "        ]"
                + "    }"
                + "],"
                + "\"max_tokens\": 300,"
                + "\"temperature\": 0"
                + "}";
    }
}