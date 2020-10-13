package at.tuwien.clients;

import at.tuwien.dto.CreateDatabaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Component
public class FdaContainerManagingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaContainerManagingClient.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    public void createDatabaseContainer(CreateDatabaseDTO dto) {
        LOGGER.debug("request fda-container-managing service for createDatabaseContainer");
        Mono<ClientResponse> response = webClientBuilder
                .build()
                .post()
                .uri("http://fda-container-managing/api/createDatabaseContainer", dto)
                .body(dto, CreateDatabaseDTO.class)
                .exchange();

        System.out.println("Hallo: "+response.toString());
    }
}
