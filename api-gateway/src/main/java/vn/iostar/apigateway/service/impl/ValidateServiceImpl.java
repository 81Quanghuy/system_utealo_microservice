package vn.iostar.apigateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.apigateway.constant.RoleName;
import vn.iostar.apigateway.dto.UserProfileResponse;
import vn.iostar.apigateway.jwt.service.JwtService;
import vn.iostar.apigateway.service.ValidateService;
import vn.iostar.apigateway.service.client.UserClientService;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserClientService userClientService;
    private final JwtService jwtService;


    boolean isValid(UserProfileResponse credentialDto) {
        return "Hoạt động".equals(credentialDto.getIsActive()) && credentialDto.getIsVerified();
    }

    @Override
    public boolean isValidUser(String accessToken) {
        String userId = jwtService.extractUserId(accessToken);
        UserProfileResponse credentialDto = userClientService.getProfileByUserId(userId);
        return isValid(credentialDto);
    }

    @Override
    public boolean isValidAdmin(String accessToken) {
        String userId = jwtService.extractUserId(accessToken);
        UserProfileResponse credentialDto = userClientService.getProfileByUserId(userId);
        if (isValid(credentialDto)) {
            return credentialDto.getRoleName().equals(RoleName.Admin);
        }
        return false;
    }
}
