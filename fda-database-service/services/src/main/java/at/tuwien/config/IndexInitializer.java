package at.tuwien.config;

import at.tuwien.entities.database.Database;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class IndexInitializer {

    private final Environment environment;
    private final ElasticsearchOperations elasticsearchOperations;

    public IndexInitializer(Environment environment, ElasticsearchOperations elasticsearchOperations) {
        this.environment = environment;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndex() {
        if (environment.acceptsProfiles(Profiles.of("test-noelastic"))) {
            return;
        }
        log.debug("creating index");
        IndexCoordinates indexCoordinates = IndexCoordinates.of("databaseindex");
        if (!elasticsearchOperations.indexOps(indexCoordinates).exists()) {
            elasticsearchOperations.indexOps(indexCoordinates).create();
            elasticsearchOperations.indexOps(indexCoordinates).createMapping(Database.class);
        }
    }
}
