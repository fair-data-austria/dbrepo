package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.table.Table;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TextDataService {

    /**
     * Reads a file and metadata into our data structure for a given table
     *
     * @param table The table.
     * @param data  The metadata.
     * @param file  The file.
     * @return Our data structure with data in it.
     * @throws IOException  File not readable.
     * @throws CsvException Problem with parsing the csv.
     */
    TableCsvDto read(Table table, TableInsertDto data, MultipartFile file) throws IOException, CsvException;
}
