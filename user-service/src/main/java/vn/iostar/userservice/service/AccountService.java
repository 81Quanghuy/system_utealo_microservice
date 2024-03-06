package vn.iostar.userservice.service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import vn.iostar.userservice.dto.request.RegisterRequest;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.Account;


import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> findAll();

    ResponseEntity<GenericResponse> userRegister(@Valid RegisterRequest registerRequest);

    String validateVerificationAccount(String token);

    Optional<Account> findByPhone(String phone);

    Optional<Account> findByEmail(String email);

    <S extends Account> S save(S entity);

    <S extends Account> List<S> saveAll(Iterable<S> entities);
}
