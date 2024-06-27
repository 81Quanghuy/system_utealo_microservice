package vn.iostar.emailservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.iostar.emailservice.dto.response.GenericResponse;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {

    @PostMapping("/verify")
    ResponseEntity<GenericResponse> verifyUser(@RequestBody String email);

}
