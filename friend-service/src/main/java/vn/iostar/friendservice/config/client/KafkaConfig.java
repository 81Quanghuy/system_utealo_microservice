package vn.iostar.friendservice.config.client;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import vn.iostar.friendservice.constant.KafkaTopicName;

@Configuration
public class KafkaConfig {
    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2L));
    }
    @Bean
    public NewTopic topic1() {
        return new NewTopic(KafkaTopicName.GET_LIST_FRIEND_BY_USERID_TOPIC, 2, (short) 1);
    }

}
