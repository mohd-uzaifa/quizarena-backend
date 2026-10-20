
package com.quizarena.quizarena.service;

import com.quizarena.quizarena.dto.GeneratedQuestion;
import com.quizarena.quizarena.dto.GeneratedQuestionsResponse;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class GeneratedQuestionsValidator {

    public void validate(
            GeneratedQuestionsResponse response,
            int expectedCount
    ) {
        if (response == null || response.getQuestions() == null) {
            throw new IllegalStateException(
                    "AI response does not contain a questions list"
            );
        }

        List<GeneratedQuestion> questions = response.getQuestions();

        if (questions.size() != expectedCount) {
            throw new IllegalStateException(
                    "AI generated " + questions.size()
                            + " questions, but " + expectedCount
                            + " were requested"
            );
        }

        Set<String> questionTexts = new HashSet<>();

        for (int i = 0; i < questions.size(); i++) {
            GeneratedQuestion question = questions.get(i);

            if (question == null) {
                throw new IllegalStateException(
                        "Question " + (i + 1) + " is empty"
                );
            }

            requireText(question.getQuestionText(), i, "question text");
            requireText(question.getOptionA(), i, "option A");
            requireText(question.getOptionB(), i, "option B");
            requireText(question.getOptionC(), i, "option C");
            requireText(question.getOptionD(), i, "option D");
            requireText(question.getExplanation(), i, "explanation");

            if (question.getCorrectOption() == null) {
                throw new IllegalStateException(
                        "Question " + (i + 1) + " has no valid correct option"
                );
            }

            Set<String> options = Set.of(
                    normalize(question.getOptionA()),
                    normalize(question.getOptionB()),
                    normalize(question.getOptionC()),
                    normalize(question.getOptionD())
            );

            if (options.size() != 4) {
                throw new IllegalStateException(
                        "Question " + (i + 1)
                                + " contains duplicate options"
                );
            }

            String normalizedQuestion =
                    normalize(question.getQuestionText());

            if (!questionTexts.add(normalizedQuestion)) {
                throw new IllegalStateException(
                        "Duplicate question detected: "
                                + question.getQuestionText()
                );
            }
        }
    }

    private void requireText(String value, int index, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Question " + (index + 1)
                            + " has an empty " + field
            );
        }
    }

    private String normalize(String value) {
        return value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
