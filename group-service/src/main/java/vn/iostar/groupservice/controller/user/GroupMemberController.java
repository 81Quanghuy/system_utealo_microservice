package vn.iostar.groupservice.controller.user;

import vn.iostar.groupservice.dto.request.*;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-members")
@Slf4j
@RequiredArgsConstructor
public class GroupMemberController {

}
