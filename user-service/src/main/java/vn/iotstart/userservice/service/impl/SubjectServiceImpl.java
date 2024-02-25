package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.request.SubjectRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Subject;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    public ResponseEntity<GenericResponse> getAllSubjects() {
        log.info("SubjectServiceImpl, getAllSubjects");
        List<SubjectResponse> subjectResponses = subjectRepository.findAll()
                .stream()
                .map(subject -> SubjectResponse.builder()
                        .id(subject.getId())
                        .name(subject.getName())
                        .build())
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all subjects successfully")
                .result(subjectResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAllSubjectsForAdmin(Integer page, Integer size) {
        log.info("SubjectServiceImpl, getAllSubjectsForAdmin");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Subject> subjectResponses = subjectRepository.findAll(pageable);
        Map<String, Object> result = Map.of(
                "subjects", subjectResponses.getContent(),
                "totalPages", subjectResponses.getTotalPages(),
                "totalElements", subjectResponses.getTotalElements(),
                "currentPage", subjectResponses.getNumber(),
                "currentElements", subjectResponses.getNumberOfElements()
        );

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all subjects successfully")
                .result(result)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> addSubject(String authorizationHeader, SubjectRequest subjectRequest) {
        log.info("SubjectServiceImpl, addSubject");
        Subject subject = subjectRepository.findByCode(subjectRequest.getCode()).orElse(null);
        if (subject != null) {
            throw new NotFoundException("Subject already exists");
        }
        subject = subjectRepository.save(Subject.builder()
                .code(subjectRequest.getCode())
                .name(subjectRequest.getName())
                .description(subjectRequest.getDescription() != null ? subjectRequest.getDescription() : "")
                .build());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Add subject successfully")
                .result(subject)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateSubject(String authorizationHeader, Integer id, SubjectRequest subjectRequest) {
        log.info("SubjectServiceImpl, updateSubject");
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) {
            throw new NotFoundException("Subject not found");
        }
        subject.setCode(subjectRequest.getCode());
        subject.setName(subjectRequest.getName());
        subject.setDescription(subjectRequest.getDescription() != null ? subjectRequest.getDescription() : "");
        subjectRepository.save(subject);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Update subject successfully")
                .result(subject)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteSubject(String authorizationHeader, Integer id) {
        log.info("SubjectServiceImpl, deleteSubject");
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) {
            throw new NotFoundException("Subject not found");
        }
        subjectRepository.delete(subject);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Delete subject successfully")
                .result(null)
                .build());
    }
}
