
package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.dto.LeaderboardEntryResponse;
import com.quizarena.quizarena.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(
            LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                leaderboardService.getLeaderboard(quizId)
        );
    }
}
