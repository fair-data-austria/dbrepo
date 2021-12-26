package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.table.Table;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TextDataService {
    TableCsvDto read(Table table, TableInsertDto data, MultipartFile file) throws IOException, CsvException;
}
