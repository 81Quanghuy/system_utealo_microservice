package vn.iostar.apigateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.apigateway.dto.CredentialDto;
import vn.iostar.apigateway.jwt.service.JwtService;
import vn.iostar.apigateway.service.ValidateService;
import vn.iostar.apigateway.service.client.UserClientService;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserClientService userClientService;
    private final JwtService jwtService;


    boolean isValid(CredentialDto credentialDto) {
        return credentialDto.getIsEnabled() &&
                credentialDto.getIsAccountNonExpired() &&
                credentialDto.getIsAccountNonLocked() &&
                credentialDto.getIsCredentialsNonExpired();
    }

    @Override
    public boolean isValidUser(String accessToken) {
        String userId = jwtService.extractUserId(accessToken);
        CredentialDto credentialDto = userClientService.getCredentialDto(userId);
        return isValid(credentialDto);
    }

    @Override
    public boolean isValidAdmin(String accessToken) {
        String userId = jwtService.extractUserId(accessToken);
        CredentialDto credentialDto = userClientService.getCredentialDto(userId);
        if (isValid(credentialDto)) {
            return credentialDto.getRole().equals("ROLE_ADMIN");
        }
        return false;
    }
}
