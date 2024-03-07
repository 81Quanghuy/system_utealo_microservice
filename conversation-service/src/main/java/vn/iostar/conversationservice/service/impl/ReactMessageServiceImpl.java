package vn.iostar.conversationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iostar.conversationservice.repository.ReactMessageRepository;
import vn.iostar.conversationservice.service.ReactMessageService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReactMessageServiceImpl implements ReactMessageService {
    private final ReactMessageRepository reactMessageRepository;
}
