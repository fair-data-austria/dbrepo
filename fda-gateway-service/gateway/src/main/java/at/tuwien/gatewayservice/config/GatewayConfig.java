package at.tuwien.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("fda-analyse-service", r -> r.path("/api/analyse/**")
                        .and()
                        .method("POST", "GET", "PUT", "DELETE")
                        .and()
                        .uri("lb://fda-analyse-service"))
                .route("fda-container-service", r -> r.path("/api/container/**",
                                "/api/image/**")
                        .and()
                        .method("POST", "GET", "PUT", "DELETE")
                        .and()
                        .uri("lb://fda-container-service"))
                .route("fda-authentication-service", r -> r.path("/api/auth/**",
                                "/api/user/**")
                        .and()
                        .method("POST", "GET", "PUT", "DELETE")
                        .and()
                        .uri("lb://fda-authentication-service"))
                .route("fda-query-service", r -> r.path("/api/database/**/metadata/**",
                                "/api/database/**/store/**")
                        .and()
                        .method("POST", "GET", "PUT", "DELETE")
                        .and()
                        .uri("lb://fda-query-service"))
                .route("fda-citation-service", r -> r.path("/api/database/**/cite/**")
                        .and()
                        .method("POST", "GET", "PUT", "DELETE")
                        .and()
                        .uri("lb://fda-citation-service"))
                .route("fda-table-service", r -> r.path("/api/database/**/table/**")
                        .and()
                        .method("POST", "GET", "PUT", "DELETE")
                        .and()
                        .uri("lb://fda-table-service"))
                .route("fda-database-service", r -> r.path("/api/database/**")
                        .and()
                        .method("POST", "GET", "PUT", "DELETE")
                        .and()
                        .uri("lb://fda-database-service"))
                .build();
    }

}
