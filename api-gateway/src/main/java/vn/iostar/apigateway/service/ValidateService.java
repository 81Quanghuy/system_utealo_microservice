package vn.iostar.apigateway.service;

public interface ValidateService {
    boolean isValidUser(String accessToken);
    boolean isValidAdmin(String accessToken);

}
