package at.tuwien.service.impl;

import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.FileStorageException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.service.CommaValueService;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
public class CommaValueServiceImpl implements CommaValueService {

    @Override
    public void replace(Table table, String location) throws FileStorageException, ImageNotSupportedException {
        final CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(table.getSeparator())
                .build();
        final Reader fileReader;
        try {
            final MultipartFile multipartFile = new MockMultipartFile(location,
                    Files.readAllBytes(Paths.get(location)));
            fileReader = new InputStreamReader(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("Failed to open buffered reader");
            throw new FileStorageException("Failed to open buffered reader", e);
        }
        final CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)
                .build();
        final CSVWriter fileWriter;
        try {
            fileWriter = new CSVWriter(new FileWriter(location), table.getSeparator(), CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        } catch (IOException e) {
            log.error("Failed to open FileWriter");
            throw new FileStorageException("Failed to open FileWriter", e);
        }
        String[] line;
        long idx = 0L;
        try {
            while ((line = reader.readNext()) != null && idx++ >= 0) {
                /* do not skip lines here yet, we replace just values */
                /* boolean */
                line = Arrays.stream(line)
                        .map(column -> {
                            if (column.equals(table.getTrueElement())) {
                                log.trace("mapped {} to boolean true", column);
                                return "1";
                            }
                            if (column.equals(table.getFalseElement())) {
                                log.trace("mapped {} to boolean false", column);
                                return "0";
                            }
                            return column;
                        }).toArray(String[]::new);
                /* null */
                line = Arrays.stream(line)
                        .map(column -> {
                            if (column.equals(table.getNullElement())) {
                                log.trace("mapped {} to null", column);
                                return null;
                            }
                            return column;
                        }).toArray(String[]::new);
                /* replace null with image specific null */
                if (!table.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
                    log.error("Currently only mariadb is supported");
                    throw new ImageNotSupportedException("Currently only mariadb is supported");
                }
                final String[] finalLine = line;
                line = Arrays.stream(line)
                        .map(column -> {
                            if (column == null || column.isEmpty()) {
                                log.trace("column is empty, replace with mariadb null representation for row {}",
                                        Arrays.toString(finalLine));
                                return "\\N";
                            }
                            return column;
                        })
                        .toArray(String[]::new);
                /* write */
                fileWriter.writeNext(line);
            }
        } catch (IOException | CsvException e) {
            log.error("Failed to read rows");
            throw new FileStorageException("Failed to read rows", e);
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            log.error("Failed to write");
            throw new FileStorageException("Failed to write", e);
        }
        log.debug("replaced {} rows", idx);
    }

}
