package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.entities.database.table.columns.TableColumnType;
import at.tuwien.exception.FileStorageException;
import com.opencsv.CSVWriter;
import org.mapstruct.Mapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.util.*;

@Mapper(componentModel = "spring")
public interface DataMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataMapper.class);

    default TableCsvDto replace(TableCsvDto data, Table table) {
        /* true element */
        table.getColumns()
                .stream()
                .filter(c -> c.getColumnType().equals(TableColumnType.BOOLEAN))
                .filter(c -> data.getData().get(c.getInternalName()).equals(table.getTrueElement()))
                .forEach(c -> data.getData().replace(c.getInternalName(), true));
        /* false element */
        table.getColumns()
                .stream()
                .filter(c -> c.getColumnType().equals(TableColumnType.BOOLEAN))
                .filter(c -> data.getData().get(c.getInternalName()).equals(table.getFalseElement()))
                .forEach(c -> data.getData().replace(c.getInternalName(), false));
        /* null element */
        table.getColumns()
                .stream()
                .filter(c -> data.getData().get(c.getInternalName()).equals(table.getNullElement()))
                .forEach(c -> data.getData().replace(c.getInternalName(), null));
        return TableCsvDto.builder()
                .data(data.getData())
                .build();
    }

    // TODO do not remove me
    default Resource resultTableToResource(QueryResultDto result, Table table) throws FileStorageException {
        /* transform data */
        final List<String[]> data = tableQueryResultDtoToStringArrayList(table, result);
        /* generate csv */
        final String filename;
        try {
            filename = Arrays.toString(MessageDigest.getInstance("MD5")
                    .digest(Instant.now()
                            .toString()
                            .getBytes()));
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm md5 not available");
            throw new FileStorageException("Failed to obtain algorithm md5", e);
        }
        /* create writers and temporary files */
        final File file;
        final FileWriter outputfile;
        try {
            file = File.createTempFile(filename, ".tmp", new File(System.getProperty("java.io.tmpdir")));
            outputfile = new FileWriter(file, true);
        } catch (IOException e) {
            log.error("Temporary file not instantiable");
            throw new FileStorageException("Failed to instantiate temporary file", e);
        }
        final CSVWriter writer = new CSVWriter(outputfile, table.getSeparator(),
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        /* write to file with column names */
        writer.writeAll(data);
        final ByteArrayResource resource;
        try {
            writer.close();
            resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error("Could not save the temporary file");
            throw new FileStorageException("Failed to save temporary file", e);
        }
        return resource;
    }

    // todo map null and boolean back
    default List<String[]> tableQueryResultDtoToStringArrayList(Table table, QueryResultDto data) {
        final List<String[]> rows = new LinkedList<>();
        final String[] headers = table.getColumns()
                .stream()
                .map(TableColumn::getName)
                .toArray(String[]::new);
        log.trace("mapped csv headers {}", Arrays.toString(headers));
        rows.add(headers);
        data.getResult()
                .forEach(row -> rows.add(Arrays.stream(row.values()
                                .toArray())
                        .map(String::valueOf)
                        .toArray(String[]::new)));
        log.trace("mapped csv rows {}", rows.size() - 1);
        return rows;
    }

}
