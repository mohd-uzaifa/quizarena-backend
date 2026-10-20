package com.quizarena.quizarena.service;

import com.quizarena.quizarena.entity.Teacher;
import com.quizarena.quizarena.repository.TeacherRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;
    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }
    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }
}
