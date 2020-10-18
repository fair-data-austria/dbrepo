package at.tuwien.client;

import at.tuwien.pojo.DatabaseConnectionDataPOJO;
import at.tuwien.dto.QueryDatabaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FdaContainerManagingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaContainerManagingClient.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    public DatabaseConnectionDataPOJO getDatabaseConnectionDataPOJO(QueryDatabaseDTO dto) {
        LOGGER.debug("request fda-container-managing service for getting database connection data");
        DatabaseConnectionDataPOJO dataPOJO = webClientBuilder
                .build()
                .get()
                .uri("http://fda-container-managing/api/getDatabaseConnectionDataByContainerID/" + dto.getContainerID())
                .retrieve()
                .bodyToMono(DatabaseConnectionDataPOJO.class)
                .block();

        return dataPOJO;

    }
}
