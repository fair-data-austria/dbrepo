package at.tuwien.mapper;

import at.tuwien.dto.table.TableBriefDto;
import at.tuwien.dto.table.TableDto;
import at.tuwien.entity.Table;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableBriefDto tableToTableBriefDto(Table data);

    TableDto tableToTableDto(Table data);

}
