package at.tuwien.mapper;

import at.tuwien.api.database.DatabaseDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.FileStorageException;
import com.opencsv.CSVWriter;
import org.mapstruct.Mapper;
import org.mariadb.jdbc.MariaDbBlob;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DatabaseMapper.class);

    Database databaseDtoToDatabase(DatabaseDto data);

}
