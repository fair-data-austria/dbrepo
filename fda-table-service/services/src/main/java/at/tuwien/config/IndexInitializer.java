package at.tuwien.config;

import at.tuwien.entities.database.Database;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Log4j2
public class IndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    public IndexInitializer(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndex() {
        log.debug("creating index");
        IndexCoordinates indexCoordinates = IndexCoordinates.of("tblindex");
        if (!elasticsearchOperations.indexOps(indexCoordinates).exists()) {
            elasticsearchOperations.indexOps(indexCoordinates).create();
            elasticsearchOperations.indexOps(indexCoordinates).createMapping(Database.class);
        }
    }
}
