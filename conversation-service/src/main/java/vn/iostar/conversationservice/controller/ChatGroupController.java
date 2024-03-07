package vn.iostar.conversationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.conversationservice.dto.ChatGroupDto;
import vn.iostar.conversationservice.service.ChatGroupService;

@RestController
@Slf4j
@RequestMapping("/api/v1/chat/group")
@RequiredArgsConstructor
public class ChatGroupController {
    private final ChatGroupService chatGroupService;

    //create ChatGroup
    @PostMapping("/create")
    public void createChatGroup(@RequestBody ChatGroupDto chatGroupDto) {
        log.info("Create ChatGroup");
        chatGroupService.createChatGroup(chatGroupDto);
    }
}
