package at.tuwien.client;

import at.tuwien.pojo.DatabaseContainer;
import at.tuwien.dto.QueryDatabaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FdaContainerManagingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaContainerManagingClient.class);


    private WebClient.Builder webClientBuilder;

    @Autowired
    public FdaContainerManagingClient(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;
    }

    public DatabaseContainer getDatabaseContainer(QueryDatabaseDTO dto) {
        LOGGER.debug("request fda-container-managing service for getting database container");
        DatabaseContainer databaseContainer = webClientBuilder
                .build()
                .get()
                .uri("http://fda-container-managing/api/getDatabaseContainerByContainerID?containerID=" + dto.getContainerID())
                .retrieve()
                .bodyToMono(DatabaseContainer.class)
                .block();

        return databaseContainer;

    }
}
