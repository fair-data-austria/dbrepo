package at.tuwien.repository.elasticsearch;

import at.tuwien.entities.database.Database;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DatabaseElasticsearchRepository 
    extends ElasticsearchRepository<Database, String> {

}
