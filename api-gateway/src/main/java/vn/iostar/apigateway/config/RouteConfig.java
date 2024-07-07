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
@RequiredArgsConstructor
public class RouteConfig {
    private final AuthFilter authFilter;
    private final AdminAuthFilter adminAuthFilter;
    private final String API_V1 = "/api/v1/";
    private final Map<String, List<String>> services = Map.of(
            "conversation-service", pathConfig(List.of("conversation", "messages","notification")),
            "friend-service", pathConfig(List.of("friend", "friend-request")),
            "group-service", pathConfig(List.of("groupPost", "group-members", "events","group-request","group")),
            "media-service", pathConfig(List.of("files", "albums")),
            "post-service", pathConfig(List.of("post", "comment", "reactions","share")),
            "user-service", pathConfig(List.of("user", "auth", "accounts", "password_reset_otp"
                    , "profiles", "roles")),
            "report-service", pathConfig(List.of("report")),
            "schedule-service", pathConfig(List.of("schedule","scheduleDetail")),
            "email-service", pathConfig(List.of("email"))
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
