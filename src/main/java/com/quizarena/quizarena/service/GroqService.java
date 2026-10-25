package com.quizarena.quizarena.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizarena.quizarena.dto.GeneratedQuestionsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final GeneratedQuestionsValidator validator;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .build();

    // Relaxed the ObjectMapper so it doesn't crash if the AI adds an unexpected field (like "explanation")
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public GroqService(GeneratedQuestionsValidator validator) {
        this.validator = validator;
    }

    public String testGroq() {
        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-20b",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", "Reply with exactly: QuizArena Groq connection successful"
                        )
                ),
                "temperature", 0
        );

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        Map<?, ?> choice = (Map<?, ?>)
                ((List<?>) response.get("choices")).get(0);

        Map<?, ?> message = (Map<?, ?>) choice.get("message");

        return message.get("content").toString();
    }

    public GeneratedQuestionsResponse generateQuestions(
            String topic,
            String difficulty,
            int questionCount,
            List<String> existingConcepts
    ) {

        String existingList = (existingConcepts == null || existingConcepts.isEmpty())
                ? "None"
                : "- " + String.join("\n- ", existingConcepts); // Fixed bullet point formatting

        String prompt = """
                Generate exactly %d multiple-choice quiz questions.

                Topic: %s
                Difficulty: %s
                
                Already covered concepts (DO NOT generate questions about these):
                %s

                Rules:
                1. Each question must have exactly four options: A, B, C, D.
                2. Exactly one option must be correct.
                3. The correctOption field must be A, B, C, or D.
                4. Match the requested difficulty level.
                5. Keep questions and options clear and brief. DO NOT write explanations.
                6. Return only valid compact JSON, without Markdown, code fences, or spaces outside the JSON object.
                7. CRITICAL: You must properly escape all internal quotation marks using backslashes (e.g., \\"example\\").
                8. CRITICAL: All four options within a question MUST be completely unique. Do not repeat the same text for multiple options.

                Required JSON structure:
                {
                  "questions": [
                    {
                      "questionText": "Question text",
                      "optionA": "First option",
                      "optionB": "Second option",
                      "optionC": "Third option",
                      "optionD": "Fourth option",
                      "correctOption": "A",
                      "concept": "Short 1-2 word core concept"
                    }
                  ]
                }
                """.formatted(questionCount, topic, difficulty, existingList);

        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-20b",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0.7, // Increased to cure writer's block and prevent duplicate options
                "max_tokens", 2500,
                "response_format", Map.of("type", "json_object")
        );

        Map<?, ?> response;
        try {
            response = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            throw new IllegalStateException(
                    "Groq API Error: The AI failed to generate valid JSON within the token limits.", e
            );
        }

        Map<?, ?> choice = (Map<?, ?>) ((List<?>) response.get("choices")).get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");
        String json = message.get("content").toString();

        GeneratedQuestionsResponse generated;
        try {
            generated = objectMapper.readValue(json, GeneratedQuestionsResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse Groq's generated questions", e);
        }

        validator.validate(generated, questionCount);

        return generated;
    }

    public com.quizarena.quizarena.dto.GeneratedQuestion generateSingleQuestion(
            String topic,
            String difficulty,
            String previousQuestion,
            List<String> existingConcepts
    ) {
        String existingList = (existingConcepts == null || existingConcepts.isEmpty())
                ? "None"
                : "- " + String.join("\n- ", existingConcepts);

        String prompt = """
            Generate exactly one multiple-choice quiz question.

            Topic: %s
            Difficulty: %s

            Previous question to replace:
            %s

            Already covered concepts (DO NOT generate questions about these):
            %s

            Rules:
            1. Generate a different question on the same topic.
            2. Match the requested difficulty.
            3. Provide exactly four options: A, B, C, and D.
            4. Exactly one option must be correct.
            5. Keep the question and options clear and brief. DO NOT write explanations.
            6. Return only valid JSON without Markdown or code fences.
            7. CRITICAL: You must properly escape all internal quotation marks using backslashes (e.g., \\"example\\").
            8. CRITICAL: All four options MUST be completely unique. Do not repeat the same text.

            Required JSON structure:
            {
              "questionText": "Question text",
              "optionA": "First option",
              "optionB": "Second option",
              "optionC": "Third option",
              "optionD": "Fourth option",
              "correctOption": "A",
              "concept": "Short 1-2 word core concept"
            }
            """.formatted(topic, difficulty, previousQuestion, existingList);

        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-20b",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0.7, // Matched the batch generator to prevent duplicate options
                "max_tokens", 1000,
                "response_format", Map.of("type", "json_object")
        );

        Map<?, ?> response;
        try {
            response = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            throw new IllegalStateException(
                    "Groq API Error: The AI failed to regenerate valid JSON within the token limits.", e
            );
        }

        Map<?, ?> choice = (Map<?, ?>) ((List<?>) response.get("choices")).get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");
        String json = message.get("content").toString();

        try {
            return objectMapper.readValue(
                    json,
                    com.quizarena.quizarena.dto.GeneratedQuestion.class
            );
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse Groq's regenerated question", e);
        }
    }
}