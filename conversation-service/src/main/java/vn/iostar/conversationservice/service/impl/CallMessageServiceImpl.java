package vn.iostar.conversationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iostar.conversationservice.repository.CallMessageRepository;
import vn.iostar.conversationservice.service.CallMessageService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallMessageServiceImpl implements CallMessageService {
    private final CallMessageRepository callMessageRepository;
}
