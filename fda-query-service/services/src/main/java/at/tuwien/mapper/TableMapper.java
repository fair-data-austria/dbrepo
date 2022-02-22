package at.tuwien.mapper;

import at.tuwien.api.database.table.TableDto;
import at.tuwien.entities.database.table.Table;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableDto tableToTableDto(Table data);

}
