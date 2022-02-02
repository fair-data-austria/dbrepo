package at.tuwien.mapper;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.entities.identifier.VisibilityType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.transaction.Transactional;

@Mapper(componentModel = "spring")
public interface IdentifierMapper {

    @Mapping(target = "creators", ignore = true)
    IdentifierDto identifierToIdentifierDto(Identifier data);

    @Mappings({
            @Mapping(target = "visibility", ignore = true),
            @Mapping(target = "creators", ignore = true)
    })
    Identifier identifierDtoToIdentifier(IdentifierDto data);

    VisibilityType visibilityTypeDtoToVisibilityType(VisibilityTypeDto data);

}
