package at.tuwien.entities;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "databaseindex")
public class Database extends at.tuwien.entities.database.Database {
}
