package at.tuwien.mapper;

import at.tuwien.dto.table.TableBriefDto;
import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.dto.table.TableDto;
import at.tuwien.entity.Table;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableBriefDto tableToTableBriefDto(Table data);

    TableDto tableToTableDto(Table data);

    Table tableCreateDtoToTable(TableCreateDto data);

}
