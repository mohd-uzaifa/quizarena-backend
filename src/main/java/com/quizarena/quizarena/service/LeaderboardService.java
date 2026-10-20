
package com.quizarena.quizarena.service;

import com.quizarena.quizarena.dto.LeaderboardEntryResponse;
import com.quizarena.quizarena.entity.Student;
import com.quizarena.quizarena.repository.StudentAnswerRepository;
import com.quizarena.quizarena.repository.StudentRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LeaderboardService {

    private final StudentRepository studentRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LeaderboardService(
            StudentRepository studentRepository,
            StudentAnswerRepository studentAnswerRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.studentRepository = studentRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<LeaderboardEntryResponse> getLeaderboard(Long quizId) {

        List<Student> students =
                studentRepository.findByQuizIdOrderByIdAsc(quizId);

        List<LeaderboardEntryResponse> leaderboard =
                new ArrayList<>();

        for (Student student : students) {
            int totalScore =
                    studentAnswerRepository.calculateTotalScore(
                            student.getId());

            leaderboard.add(
                    new LeaderboardEntryResponse(
                            student.getId(),
                            student.getName(),
                            totalScore,
                            0
                    )
            );
        }

        leaderboard.sort(
                Comparator.comparingInt(
                                LeaderboardEntryResponse::getTotalScore
                        ).reversed()
                        .thenComparing(
                                LeaderboardEntryResponse::getStudentName,
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        List<LeaderboardEntryResponse> rankedLeaderboard =
                new ArrayList<>();

        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntryResponse entry = leaderboard.get(i);

            rankedLeaderboard.add(
                    new LeaderboardEntryResponse(
                            entry.getStudentId(),
                            entry.getStudentName(),
                            entry.getTotalScore(),
                            i + 1
                    )
            );
        }

        return rankedLeaderboard;
    }
    public void broadcastLeaderboard(Long quizId) {
        List<LeaderboardEntryResponse> leaderboard =
                getLeaderboard(quizId);

        messagingTemplate.convertAndSend(
                "/topic/quiz/" + quizId + "/leaderboard",
                leaderboard
        );
    }
}
