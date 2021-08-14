package at.tuwien.mapper;

import at.tuwien.entities.database.Database;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmqpMapper {

    default String exchangeName(Database database) {
        return "fda.c" + database.getContainer().getId() + ".d" + database.getId();
    }

    default String queueName(Database database) {
        return exchangeName(database) + ".q" + 1;
    }

}
