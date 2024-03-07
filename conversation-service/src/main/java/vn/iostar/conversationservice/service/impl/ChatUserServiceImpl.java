package vn.iostar.conversationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iostar.conversationservice.repository.ChatUserRepository;
import vn.iostar.conversationservice.service.ChatUserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatUserServiceImpl implements ChatUserService {
    private final ChatUserRepository chatUserRepository;
}
