package com.trvankiet.app.controller.user;

import com.trvankiet.app.dto.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Slf4j

public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<GenericResponse> getAllSubjects() {
        log.info("AdminSubjectController, getAllSubjects");
        return subjectService.getAllSubjects();
    }
}
