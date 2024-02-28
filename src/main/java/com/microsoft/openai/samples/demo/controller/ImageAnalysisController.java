package com.microsoft.openai.samples.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.openai.samples.demo.model.Response;
import com.microsoft.openai.samples.demo.service.ImageAnalysisService;

@RestController
@RequestMapping("/api/image-analysis")
public class ImageAnalysisController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAnalysisController.class);

    @Autowired
    private ImageAnalysisService imageAnalysisService;

        @PostMapping("/upload")
        @CrossOrigin(origins = "*")
        public Response analyzeImage(@RequestParam("file") MultipartFile file, @RequestHeader("expenseType") String expenseType) {
            try {
                return imageAnalysisService.analyzeImage(file, expenseType);
            } catch (RuntimeException e) {
                LOGGER.error("Error processing image: {}", e.getMessage());
                throw e;
            }
        }
    }
