package at.tuwien.mapper;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmqpMapper {

    default String exchangeName(Database database) {
        return "fda." + database.getInternalName();
    }

    default String queueName(Table table) {
        return exchangeName(table.getDatabase()) + "." + table.getInternalName();
    }

}
