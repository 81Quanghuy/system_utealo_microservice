package vn.iostar.userservice.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollingServiceImpl {

    @Scheduled(fixedRate = 60000)
    public void polling() {
        System.out.println("Polling...");
    }
}
