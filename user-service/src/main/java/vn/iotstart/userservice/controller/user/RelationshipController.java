package com.trvankiet.app.controller.user;

import com.trvankiet.app.dto.request.CreateRelationRequest;
import com.trvankiet.app.dto.request.UpdateRelationRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/relationships")
@Slf4j
@RequiredArgsConstructor
public class RelationshipController {

    private final JwtService jwtService;
    private final RelationService relationService;

    @PostMapping
    public ResponseEntity<GenericResponse> createRelationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestBody @Valid CreateRelationRequest createRelationRequest) {
        log.info("AdminRelationshipController, createRelationRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.createRelationRequest(userId, createRelationRequest);
    }

    @GetMapping("/student/relationship-requests")
    public ResponseEntity<GenericResponse> getRelationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("AdminRelationshipController, getRelationRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.getRelationRequest(userId);
    }

    @GetMapping("/parent/relationship-requests")
    public ResponseEntity<GenericResponse> getParentRelationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("AdminRelationshipController, getRelationRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.getParentRelationRequest(userId);
    }

    @GetMapping
    public ResponseEntity<GenericResponse> getRelationships(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("AdminRelationshipController, getRelationships");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.getRelationships(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse> updateRelationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("id") String id, @RequestBody @Valid UpdateRelationRequest updateRelationRequest) {
        log.info("AdminRelationshipController, updateRelationRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.updateRelationRequest(userId, id, updateRelationRequest);
    }

}
