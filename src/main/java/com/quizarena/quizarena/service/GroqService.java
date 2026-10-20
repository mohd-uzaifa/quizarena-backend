package com.quizarena.quizarena.service;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            int questionCount
    ) {
        String prompt = """
                Generate exactly %d multiple-choice quiz questions.

                Topic: %s
                Difficulty: %s

                Rules:
                1. Each question must have exactly four options: A, B, C, D.
                2. Exactly one option must be correct.
                3. The correctOption field must be A, B, C, or D.
                4. Questions must match the requested difficulty.
                5. Avoid duplicate questions.
                6. Keep questions and options clear and factually accurate.
                7. Include a short explanation for each correct answer.
                8. Return only valid JSON, without Markdown or code fences.
                9. CRITICAL: You must properly escape all internal quotation marks using backslashes (e.g., \\"example\\"). Do not use raw newlines inside JSON string values.

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
                      "explanation": "Why the answer is correct"
                    }
                  ]
                }
                """.formatted(questionCount, topic, difficulty);

        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-20b",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0.3,
                "max_tokens",4000,
                "response_format", Map.of("type", "json_object")
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
        String json = message.get("content").toString();

        GeneratedQuestionsResponse generated;

        try {
            generated = objectMapper.readValue(
                    json,
                    GeneratedQuestionsResponse.class
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not parse Groq's generated questions",
                    e
            );
        }

        validator.validate(generated, questionCount);

        return generated;
    }


    public com.quizarena.quizarena.dto.GeneratedQuestion generateSingleQuestion(
            String topic,
            String difficulty,
            String previousQuestion,
            List<String> existingQuestions
    ) {
        String existingList = existingQuestions.isEmpty()
                ? "None"
                : "- " + String.join("\n- ", existingQuestions);

        String prompt = """
            Generate exactly one multiple-choice quiz question.

            Topic: %s
            Difficulty: %s

            Previous question to replace:
            %s

            Existing questions already in the quiz (DO NOT duplicate any of these):
            %s

            Rules:
            1. Generate a different question on the same topic.
            2. Match the requested difficulty.
            3. Provide exactly four options: A, B, C, and D.
            4. Exactly one option must be correct.
            5. Include a short explanation.
            6. Return only valid JSON without Markdown or code fences.
            7. CRITICAL: You must properly escape all internal quotation marks using backslashes (e.g., \\"example\\"). Do not use raw newlines inside JSON string values.

            Required JSON structure:
            {
              "questionText": "Question text",
              "optionA": "First option",
              "optionB": "Second option",
              "optionC": "Third option",
              "optionD": "Fourth option",
              "correctOption": "A",
              "explanation": "Why the answer is correct"
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
                "temperature", 0.4,
                "max_tokens", 4000,
                "response_format", Map.of("type", "json_object")
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
        String json = message.get("content").toString();

        try {
            return objectMapper.readValue(
                    json,
                    com.quizarena.quizarena.dto.GeneratedQuestion.class
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not parse Groq's regenerated question",
                    e
            );
        }
    }
}