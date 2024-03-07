package vn.iostar.friendservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iostar.friendservice.dto.CredentialDto;
import vn.iostar.friendservice.dto.UserDto;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/users")
public interface UserClientService {

    @GetMapping("/userDto/{uId}")
    UserDto getUserDtoByUserId(@PathVariable String uId);

    @GetMapping("/credentials")
    CredentialDto getCredentialDtoByUserId(@RequestParam String uId);

}
