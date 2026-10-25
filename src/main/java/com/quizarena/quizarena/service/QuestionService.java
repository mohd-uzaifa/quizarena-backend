package com.quizarena.quizarena.service;

import com.quizarena.quizarena.dto.GeneratedQuestionsResponse;
import com.quizarena.quizarena.dto.QuestionResponse;
import com.quizarena.quizarena.entity.Question;
import com.quizarena.quizarena.entity.Quiz;
import com.quizarena.quizarena.entity.QuizStatus;
import com.quizarena.quizarena.exception.ResourceNotFoundException;
import com.quizarena.quizarena.repository.QuestionRepository;
import com.quizarena.quizarena.repository.QuizRepository;
import org.springframework.stereotype.Service;
import com.quizarena.quizarena.dto.GeneratedQuestion;
import com.quizarena.quizarena.dto.LiveQuestionResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final GroqService groqService;
    private final SimpMessagingTemplate messagingTemplate;

    public QuestionService(
            QuestionRepository questionRepository,
            QuizRepository quizRepository,
            GroqService groqService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.groqService = groqService;
        this.messagingTemplate = messagingTemplate;
    }

    public Question addQuestion(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new IllegalStateException("Questions can only be added to draft quizzes");
        }

        long questionCount = questionRepository.countByQuizId(quizId);
        if (questionCount >= 15) {
            throw new IllegalStateException("A quiz cannot contain more than 15 questions");
        }

        question.setQuiz(quiz);
        question.setRegenerationCount(0);
        return questionRepository.save(question);
    }

    public List<Question> getQuestionByQuizId(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found");
        }
        return questionRepository.findByQuizIdOrderByIdAsc(quizId);
    }

    public Question updateQuestion(Long quizId, Long questionId, Question updatedQuestion) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!existingQuestion.getQuiz().getId().equals(quiz.getId())) {
            throw new IllegalArgumentException("Question does not belong to this quiz");
        }

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new IllegalStateException("Only draft quizzes can be edited");
        }

        existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
        existingQuestion.setOptionA(updatedQuestion.getOptionA());
        existingQuestion.setOptionB(updatedQuestion.getOptionB());
        existingQuestion.setOptionC(updatedQuestion.getOptionC());
        existingQuestion.setOptionD(updatedQuestion.getOptionD());
        existingQuestion.setCorrectOption(updatedQuestion.getCorrectOption());

        return questionRepository.save(existingQuestion);
    }

    public void deleteQuestion(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getQuiz().getId().equals(quiz.getId())) {
            throw new IllegalArgumentException("Question does not belong to this quiz");
        }

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new IllegalStateException("Only draft quizzes can be edited");
        }

        questionRepository.delete(question);
    }

    public List<QuestionResponse> generateQuestionsForQuiz(
            Long quizId,
            int questionCount,
            List<String> existingConcepts // NEW: Receive concepts from Controller
    ) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new IllegalStateException("AI questions can only be generated for draft quizzes");
        }

        // CRITICAL: Lowered minimum from 5 to 1 to allow smaller final batches
        if (questionCount < 1 || questionCount > 15) {
            throw new IllegalArgumentException("Question count must be between 1 and 15");
        }

        long existingCount = questionRepository.countByQuizId(quizId);
        if (existingCount + questionCount > 15) {
            throw new IllegalStateException("A quiz cannot contain more than 15 questions");
        }

        // NEW: Pass the frontend's concept list directly to Groq
        GeneratedQuestionsResponse generated =
                groqService.generateQuestions(
                        quiz.getTopic(),
                        quiz.getDifficulty().name(),
                        questionCount,
                        existingConcepts
                );

        List<QuestionResponse> savedQuestions = new ArrayList<>();

        for (var generatedQuestion : generated.getQuestions()) {
            Question question = new Question();

            question.setQuestionText(generatedQuestion.getQuestionText());
            question.setOptionA(generatedQuestion.getOptionA());
            question.setOptionB(generatedQuestion.getOptionB());
            question.setOptionC(generatedQuestion.getOptionC());
            question.setOptionD(generatedQuestion.getOptionD());
            question.setCorrectOption(generatedQuestion.getCorrectOption());
            question.setQuiz(quiz);
            question.setRegenerationCount(0);

            Question saved = questionRepository.save(question);

            savedQuestions.add(
                    new QuestionResponse(
                            saved.getId(),
                            saved.getQuestionText(),
                            saved.getOptionA(),
                            saved.getOptionB(),
                            saved.getOptionC(),
                            saved.getOptionD(),
                            saved.getCorrectOption(),
                            saved.getRegenerationCount(),
                            quiz.getId(),
                            generatedQuestion.getConcept() // NEW: Pass the concept back to React
                    )
            );
        }

        return savedQuestions;
    }

    public QuestionResponse regenerateQuestion(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new IllegalStateException("Only draft quizzes can have questions regenerated");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getQuiz().getId().equals(quiz.getId())) {
            throw new IllegalArgumentException("Question does not belong to this quiz");
        }

        int regenerationCount = question.getRegenerationCount() == null ? 0 : question.getRegenerationCount();
        if (regenerationCount >= 3) {
            throw new IllegalStateException("A question can only be regenerated 3 times");
        }

        List<Question> existingQuestions = questionRepository.findByQuizIdOrderByIdAsc(quizId);
        List<String> existingQuestionTexts = existingQuestions.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());

        GeneratedQuestion generated =
                groqService.generateSingleQuestion(
                        quiz.getTopic(),
                        quiz.getDifficulty().name(),
                        question.getQuestionText(),
                        existingQuestionTexts
                );

        question.setQuestionText(generated.getQuestionText());
        question.setOptionA(generated.getOptionA());
        question.setOptionB(generated.getOptionB());
        question.setOptionC(generated.getOptionC());
        question.setOptionD(generated.getOptionD());
        question.setCorrectOption(generated.getCorrectOption());
        question.setRegenerationCount(regenerationCount + 1);

        Question saved = questionRepository.save(question);

        return new QuestionResponse(
                saved.getId(),
                saved.getQuestionText(),
                saved.getOptionA(),
                saved.getOptionB(),
                saved.getOptionC(),
                saved.getOptionD(),
                saved.getCorrectOption(),
                saved.getRegenerationCount(),
                quiz.getId(),
                generated.getConcept() // NEW
        );
    }

    public void broadcastQuestion(Long quizId, int questionNumber) {
        // [Existing broadcastQuestion logic remains identical]
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.LIVE) {
            throw new IllegalStateException("Quiz is not live");
        }
        quiz.setCurrentQuestionNumber(questionNumber);
        quiz.setCurrentQuestionStartedAt(LocalDateTime.now());
        quizRepository.save(quiz);

        List<Question> questions = questionRepository.findByQuizIdOrderByIdAsc(quizId);

        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions found for this quiz");
        }

        if (questionNumber < 1 || questionNumber > questions.size()) {
            throw new IllegalArgumentException("Invalid question number");
        }

        Question question = questions.get(questionNumber - 1);

        int timeLimitSeconds = switch (quiz.getDifficulty()) {
            case EASY -> 15;
            case MEDIUM -> 20;
            case HARD -> 30;
        };

        LiveQuestionResponse response = new LiveQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                questionNumber,
                questions.size(),
                timeLimitSeconds
        );

        messagingTemplate.convertAndSend("/topic/quiz/" + quizId + "/question", response);
    }
}