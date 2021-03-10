package at.tuwien.client;

import at.tuwien.mapper.CreateTableViaCsvToExecuteStatementMapper;
import at.tuwien.model.CSVColumnsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FdaAnalyseServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaQueryServiceClient.class);

    private WebClient.Builder webClientBuilder;
    private CreateTableViaCsvToExecuteStatementMapper mapper;

    @Autowired
    public FdaAnalyseServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public CSVColumnsResult determineDatatypes(String pathToCSVFile) {
        LOGGER.debug("request analyse-service for getting determined datatypes");
        CSVColumnsResult csvColumnsResult = webClientBuilder
                .build()
                .get()
                .uri("http://fda-analyse-service/datatypesbypath?filepath=" + pathToCSVFile)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CSVColumnsResult.class)
                .block();
        return csvColumnsResult;

    }
}
