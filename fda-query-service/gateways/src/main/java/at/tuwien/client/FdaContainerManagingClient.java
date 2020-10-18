package at.tuwien.client;

import at.tuwien.dto.QueryDatabaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FdaContainerManagingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaContainerManagingClient.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    public boolean createDatabaseContainer(QueryDatabaseDTO dto) {
   /*     LOGGER.debug("request fda-container-managing service for createDatabaseContainer");
        ClientResponse clientResponse = webClientBuilder
                .build()
                .get()
                .uri("http://fda-container-managing/api/getDatabaseConnectionDataByContainerID")
                .
                .body(Mono.just(dto), CreateDatabaseDTO.class)
                .exchange()
                .block();

        if (clientResponse.statusCode().is2xxSuccessful()) {
            return true;
        }*/
        return false;
    }
}
