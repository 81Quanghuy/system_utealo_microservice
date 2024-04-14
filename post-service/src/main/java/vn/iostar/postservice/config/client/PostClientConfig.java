package vn.iostar.postservice.config.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PostClientConfig {

    @LoadBalanced
    @Bean
    public RestTemplate postRestTemplate() {
        return new RestTemplate();
    }

}
