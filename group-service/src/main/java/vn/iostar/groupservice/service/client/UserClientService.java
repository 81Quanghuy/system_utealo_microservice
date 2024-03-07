package vn.iostar.groupservice.service.client;

import vn.iostar.groupservice.dto.CredentialDto;
import vn.iostar.groupservice.dto.SimpleUserDto;
import vn.iostar.groupservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/users")
public interface UserClientService {



}
