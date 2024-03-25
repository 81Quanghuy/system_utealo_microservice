package vn.iostar.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class RouteConfig {

    @Autowired
    private AuthFilter authFilter;
    @Autowired
    private AdminAuthFilter adminAuthFilter;
    private final String API_V1 = "/api/v1/";
    private final Map<String, List<String>> services = Map.of(
            "conversation-service", pathConfig(List.of("chat-messages", "chat-users",
                    "chat-rooms", "notifications")),
            "friend-service", pathConfig(List.of("friends", "friend-requests",
                    "friendships")),
            "group-service", pathConfig(List.of("groups", "group-members", "events",
                    "group-member-requests", "group-member-invitations")),
            "media-service", pathConfig(List.of("files", "albums")),
            "post-service", pathConfig(List.of("posts", "comments", "reactions")),
            "user-service", pathConfig(List.of("users", "credentials", "tokens", "auth", "relationships"
                    , "addresses", "subjects")),
            "exam-service", pathConfig(List.of("exams", "questions",
                    "answers", "submissions", "submission-details")
            )
    );

    private List<String> pathConfig(List<String> paths) {
        return paths.stream()
                .map(path -> API_V1 + path + "/**")
                .collect(Collectors.toList());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();
        for (Map.Entry<String, List<String>> entry : services.entrySet()) {
            String serviceName = entry.getKey();
            List<String> servicePath = entry.getValue();
            routes.route(serviceName, r -> r
                    .path("/v3/api-docs/" + serviceName)
                    .filters(f -> f.filter(authFilter))
                    .uri("lb://" + serviceName));
            for (String path : servicePath) {
                routes.route(serviceName, r -> r
                        .path(path)
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://" + serviceName)
                );
                // /api/v1/users/**
                routes.route(serviceName, r -> r
                        .path(path.substring(0, path.indexOf("/**")) + "/admin/**")
                        .filters(f -> f.filter(adminAuthFilter))
                        .uri("lb://" + serviceName)
                );
            }
        }

        return routes.build();
    }
}
