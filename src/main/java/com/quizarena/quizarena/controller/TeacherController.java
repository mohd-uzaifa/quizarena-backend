
package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.dto.TeacherResponse;
import com.quizarena.quizarena.entity.Teacher;
import com.quizarena.quizarena.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(
            @RequestBody Teacher teacher) {

        Teacher savedTeacher = teacherService.createTeacher(teacher);

        TeacherResponse response = new TeacherResponse(
                savedTeacher.getId(),
                savedTeacher.getName(),
                savedTeacher.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}