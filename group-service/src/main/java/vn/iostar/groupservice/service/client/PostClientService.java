package vn.iostar.groupservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "post-service", contextId = "postClientService", path = "/api/v1/posts")
public interface PostClientService {
}
