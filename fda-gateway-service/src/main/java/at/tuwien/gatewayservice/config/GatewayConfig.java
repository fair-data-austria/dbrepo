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
                .route("database-managing-at.tuwien.service", r -> r.path("/database/**")
                        .and()
                        .method("POST","GET")
                        .and()
                        .uri("lb://FDA-Database-Managing"))
                .route("table-at.tuwien.service", r -> r.path("/table/**")
                        .and()
                        .method("POST","GET")
                        .and()
                        .uri("lb://FDA-Table-Service"))
                .route("query-at.tuwien.service", r -> r.path("/query/executeQuery")
                        .and()
                        .method("POST")
                        .and()
                        .uri("lb://FDA-Query-Service"))
                .build();
    }

}
