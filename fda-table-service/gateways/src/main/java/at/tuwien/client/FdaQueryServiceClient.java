package at.tuwien.client;

import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.mapper.ContainerIdAndQueryToExecuteInternalQueryMapper;
import at.tuwien.mapper.CreateTableViaCsvToExecuteStatementMapper;
import at.tuwien.model.CreateCSVTableWithDataset;
import at.tuwien.model.ExecuteInternalQueryDTO;
import at.tuwien.model.ExecuteStatementDTO;
import at.tuwien.model.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FdaQueryServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaQueryServiceClient.class);


    private WebClient.Builder webClientBuilder;
    private CreateTableViaCsvToExecuteStatementMapper mapper;

    @Autowired
    public FdaQueryServiceClient(WebClient.Builder webClientBuilder, CreateTableViaCsvToExecuteStatementMapper mapper) {
        this.webClientBuilder = webClientBuilder;
        this.mapper = mapper;
    }

    public boolean executeStatement(CreateTableViaCsvDTO dto, String statement) {
        LOGGER.debug("request fda-query-service for executing statement");
        ExecuteStatementDTO statementDTO = mapper.map(dto, statement);
        ClientResponse response = webClientBuilder
                .build()
                .post()
                .uri("http://fda-query-service/api/executeStatement")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(statementDTO), ExecuteStatementDTO.class)
                .exchange()
                .block();

        return true;
    }

    public QueryResult executeQuery(String containerID, String query) {
        ContainerIdAndQueryToExecuteInternalQueryMapper mapper = new ContainerIdAndQueryToExecuteInternalQueryMapper();
        ExecuteInternalQueryDTO execInternalQueryDTO = mapper.map(containerID, query);
        QueryResult queryResult = webClientBuilder
                .build()
                .post()
                .uri("http://fda-query-service/api/executeQuery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(execInternalQueryDTO), ExecuteInternalQueryDTO.class)
                .retrieve()
                .bodyToMono(QueryResult.class)
                .block();

        return queryResult;

    }

    public void copyCSVIntoTable(CreateCSVTableWithDataset tableWithDataset) {
        ClientResponse response = webClientBuilder
                .build()
                .post()
                .uri("http://fda-query-service/api/copyCSVIntoTable")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(tableWithDataset), CreateCSVTableWithDataset.class)
                .exchange()
                .block();
    }
}
