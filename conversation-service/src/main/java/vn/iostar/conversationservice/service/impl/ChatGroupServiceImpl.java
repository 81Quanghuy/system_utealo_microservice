package vn.iostar.conversationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iostar.conversationservice.dto.ChatGroupDto;
import vn.iostar.conversationservice.entity.ChatGroup;
import vn.iostar.conversationservice.repository.ChatGroupRepository;
import vn.iostar.conversationservice.service.ChatGroupService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatGroupServiceImpl implements ChatGroupService {
    private final ChatGroupRepository chatGroupRepository;


    @Override
    public void createChatGroup(ChatGroupDto chatGroupDto) {
        ChatGroup chatGroup = ChatGroup.builder()
                .name(chatGroupDto.getName())
                .avatarUrl(chatGroupDto.getAvatarUrl())
                .authorId(chatGroupDto.getAuthorId())
                .id(UUID.randomUUID().toString())
                .isAcceptAllRequest(chatGroupDto.getIsAcceptAllRequest())
                .members(chatGroupDto.getMembers())
                .build();
        chatGroupRepository.save(chatGroup);

    }
}
