package vn.iostar.friendservice.config.client;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(groupId = "friend-kafka", topics = "topic2")
public class MyKafkaListener {

    @KafkaListener(topics = "topic2")
    public void listen(String message) {
        System.out.println("Received Messasge in group - group-id: " + message);
    }
}
