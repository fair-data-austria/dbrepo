package at.tuwien.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FlinkConfig {

    @Value("${fda.stream.database_id}")
    private Long streamDatabaseId;

    @Value("${fda.stream.table_id}")
    private Long streamTableId;

}
