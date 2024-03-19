package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.SharePostRequestDTO;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.service.ShareService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share")
public class ShareController {

    private final ShareService shareService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<Object> createSharePost(@RequestBody SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String token) {
        return shareService.sharePost(token, requestDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateSharePost(@RequestBody SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String authorizationHeader,
                                                  BindingResult bindingResult) throws Exception {

        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return shareService.updateSharePost(requestDTO, currentUserId);

    }

    @PutMapping("/delete/{shareId}")
    public ResponseEntity<GenericResponse> deleteSharePost(@RequestHeader("Authorization") String token,
                                                           @PathVariable("shareId") String shareId, @RequestBody String userId) {
        return shareService.deleteSharePost(shareId, token, userId);

    }

}
