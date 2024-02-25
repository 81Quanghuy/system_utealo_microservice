package com.trvankiet.app.controller.user;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.service.CredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credentials")
@Slf4j
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping("/{id}")
    public CredentialDto findById(@PathVariable final String id) {
        log.info("CredentialController Get, CredentialDto, findById");
        return credentialService.findByIdDto(id);
    }

}
