package at.tuwien.clients;

import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.model.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class FdaContainerManagingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaContainerManagingClient.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    public boolean createDatabaseContainer(DatabaseCreateDto dto)  {
        LOGGER.debug("request fda-container-managing service for createDatabaseContainer");
        ClientResponse clientResponse = webClientBuilder
                .build()
                .post()
                .uri("http://fda-container-managing/at.tuwien.api/createDatabaseContainer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), DatabaseCreateDto.class)
                .exchange()
                .block();

        if (clientResponse.statusCode().is2xxSuccessful()) {
            return true;
        }
        return false;

    }

    public List<Database> getCreatedDatabases() {
        LOGGER.debug("request fda-container-managing service for getting all created databases");
        List<Database> databases = webClientBuilder
                .build()
                .get()
                .uri("http://fda-container-managing/at.tuwien.api/getCreatedDatabaseContainers")
                .retrieve()
                .bodyToFlux(Database.class)
                .collectList()
                .block();

       return databases;

    }
}
