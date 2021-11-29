package at.tuwien.mapper;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.query.File;
import at.tuwien.entities.database.query.Query;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    File fileDtoToFile(FileDto data);

    FileDto fileToFileDto(File data);

}
