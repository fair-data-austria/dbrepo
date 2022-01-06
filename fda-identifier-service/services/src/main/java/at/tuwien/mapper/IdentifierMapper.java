package at.tuwien.mapper;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.entities.identifier.Identifier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdentifierMapper {

    IdentifierDto identifierToIdentifierDto(Identifier data);

    Identifier identifierDtoToIdentifier(IdentifierDto data);

}
