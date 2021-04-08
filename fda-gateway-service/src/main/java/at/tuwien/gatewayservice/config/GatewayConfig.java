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
                .route("container-service", r -> r.path("/container/[0-9]*")
                        .and()
                        .uri("lb://fda-container-service"))
                .route("table-service", r -> r.path("/database/[0-9]*/table/[0-9]*")
                        .and()
                        .uri("lb://fda-table-service"))
                .route("database-service", r -> r.path("/database/[0-9]*")
                        .and()
                        .uri("lb://fda-database-service"))
                .route("query-service", r -> r.path("/query/[0-9]*")
                        .and()
                        .uri("lb://fda-query-service"))
                .build();
    }

}
