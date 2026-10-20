package com.quizarena.quizarena.service;

import com.quizarena.quizarena.dto.JoinQuizRequest;
import com.quizarena.quizarena.dto.StudentResponse;
import com.quizarena.quizarena.entity.Quiz;
import com.quizarena.quizarena.entity.QuizStatus;
import com.quizarena.quizarena.entity.Student;
import com.quizarena.quizarena.exception.ResourceNotFoundException;
import com.quizarena.quizarena.repository.QuizRepository;
import com.quizarena.quizarena.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.quizarena.quizarena.dto.LobbyStudentResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final QuizRepository quizRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public StudentService(StudentRepository studentRepository,
                          QuizRepository quizRepository,
                          SimpMessagingTemplate messagingTemplate) {
        this.studentRepository = studentRepository;
        this.quizRepository = quizRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public StudentResponse joinQuiz(JoinQuizRequest request) {

        String roomCode = request.getRoomCode().trim().toUpperCase();
        String studentName = request.getStudentName().trim();

        Quiz quiz = quizRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid room code"));

        if (quiz.getStatus() != QuizStatus.WAITING) {
            throw new IllegalStateException(
                    "This quiz is not accepting students");
        }

        if (studentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Student name cannot be blank");
        }

        if (studentRepository.existsByQuizIdAndNameIgnoreCase(
                quiz.getId(), studentName)) {
            throw new IllegalArgumentException(
                    "This name is already taken in the lobby");
        }

        Student student = new Student(studentName);
        student.setQuiz(quiz);


        Student savedStudent = studentRepository.save(student);

        messagingTemplate.convertAndSend(
                "/topic/quiz/" + quiz.getId() + "/lobby",
                new LobbyStudentResponse(
                        savedStudent.getId(),
                        savedStudent.getName(),
                        quiz.getRoomCode()
                )
        );

        return new StudentResponse(
                savedStudent.getId(),
                savedStudent.getName(),
                quiz.getRoomCode(),
                quiz.getId()
        );

    }

    public java.util.List<StudentResponse> getStudentsByQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        return studentRepository.findByQuizIdOrderByIdAsc(quizId)
                .stream()
                .map(student -> new StudentResponse(
                        student.getId(),
                        student.getName(),
                        quiz.getRoomCode(),
                        quiz.getId()
                ))
                .toList();
    }
}