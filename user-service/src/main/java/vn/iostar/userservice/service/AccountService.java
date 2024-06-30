package vn.iostar.userservice.service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import vn.iostar.userservice.dto.LoginDTO;
import vn.iostar.userservice.dto.request.RegisterRequest;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.Account;


import java.util.List;
import java.util.Optional;

public interface AccountService {


    // Tìm kiếm tất cả tài khoản
    List<Account> findAll();

    // Đăng ký tài khoản
    ResponseEntity<GenericResponse> userRegister(@Valid RegisterRequest registerRequest);

    // Tìm kiếm tài khoản theo số điện thoại
    Optional<Account> findByPhone(String phone);

    // Tìm kiếm tài khoản theo email
    Optional<Account> findByEmail(String email);

    // Lưu tài khoản
    <S extends Account> S save(S entity);

    // Lưu tất cả tài khoản
    <S extends Account> void saveAll(Iterable<S> entities);

    ResponseEntity<GenericResponse> login(LoginDTO loginDTO);
    ResponseEntity<GenericResponse> sendOTP(String email);

    ResponseEntity<GenericResponse> verifyParent(String token);
}
