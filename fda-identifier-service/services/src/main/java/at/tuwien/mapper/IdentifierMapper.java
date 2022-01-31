package at.tuwien.mapper;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.entities.identifier.VisibilityType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IdentifierMapper {

    IdentifierDto identifierToIdentifierDto(Identifier data);

    @Mappings({
            @Mapping(target = "visibility", ignore = true)
    })
    Identifier identifierDtoToIdentifier(IdentifierDto data);

    VisibilityType visibilityTypeDtoToVisibilityType(VisibilityTypeDto data);

}
