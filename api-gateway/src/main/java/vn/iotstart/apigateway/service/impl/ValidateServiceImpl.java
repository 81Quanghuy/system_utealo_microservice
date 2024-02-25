package vn.iotstart.apigateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iotstart.apigateway.dto.CredentialDto;
import vn.iotstart.apigateway.jwt.service.JwtService;
import vn.iotstart.apigateway.service.ValidateService;
import vn.iotstart.apigateway.service.client.UserClientService;

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
