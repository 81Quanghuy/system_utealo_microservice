package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.request.SubjectRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subjects/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminSubjectController {

    private final SubjectService subjectService;

    @GetMapping("/get-all-subjects")
    public ResponseEntity<GenericResponse> getAllSubjects(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                          @RequestHeader(value = "page", defaultValue = "0") Integer page,
                                                          @RequestHeader(value = "size", defaultValue = "10") Integer size) {
        log.info("AdminSubjectController, getAllSubjects");
        return subjectService.getAllSubjectsForAdmin(page, size);
    }

    @PostMapping("/add-subject")
    public ResponseEntity<GenericResponse> addSubject(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                      @RequestBody SubjectRequest subjectRequest) {
        log.info("AdminSubjectController, addSubject");
        return subjectService.addSubject(authorizationHeader, subjectRequest);
    }

    @PutMapping("/update-subject/{id}")
    public ResponseEntity<GenericResponse> updateSubject(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                         @PathVariable("id") Integer id,
                                                         @RequestBody SubjectRequest subjectRequest) {
        log.info("AdminSubjectController, updateSubject");
        return subjectService.updateSubject(authorizationHeader, id, subjectRequest);
    }

    @DeleteMapping("/delete-subject/{id}")
    public ResponseEntity<GenericResponse> deleteSubject(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                         @PathVariable("id") Integer id) {
        log.info("AdminSubjectController, deleteSubject");
        return subjectService.deleteSubject(authorizationHeader, id);
    }

}
