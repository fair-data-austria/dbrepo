package at.tuwien.clients;

import at.tuwien.dto.CreateDatabaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    public boolean createDatabaseContainer(CreateDatabaseDTO dto)  {
        LOGGER.debug("request fda-container-managing service for createDatabaseContainer");
        ClientResponse clientResponse = webClientBuilder
                .build()
                .post()
                .uri("http://fda-container-managing/api/createDatabaseContainer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), CreateDatabaseDTO.class)
                .exchange()
                .block();

        if (clientResponse.statusCode().is2xxSuccessful()) {
            return true;
        }
        return false;


//                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class)
//                        .doOnSuccess(body -> {
//                            if (clientResponse.statusCode().isError()) {
//                                LOGGER.error("HttpStatusCode = {}", clientResponse.statusCode());
//                                LOGGER.error("HttpHeaders = {}", clientResponse.headers().asHttpHeaders());
//                                LOGGER.error("ResponseBody = {}", body);
//                            }
//                        }))
//                .block();

    }
}
