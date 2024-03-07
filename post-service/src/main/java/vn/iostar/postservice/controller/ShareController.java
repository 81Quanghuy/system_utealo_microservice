package vn.iostar.postservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.service.ShareService;

@RestController
@RequestMapping("/api/v1/share")
public class ShareController {

    @Autowired
    ShareService shareService;

    @PostMapping("/create")
    public ResponseEntity<Share> createShare() {
        return ResponseEntity.ok(shareService.createShare());
    }
}
