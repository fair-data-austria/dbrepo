package at.tuwien.controller;

import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.service.TableService;
import com.opencsv.CSVReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TableController {

    private TableService service;

    @Autowired
    public TableController(TableService service){
        this.service = service;
    }



    @PostMapping("/fileupload")
    public void processUpload(@RequestParam MultipartFile file) throws IOException {
        // process your file

        if("text/csv".equals(file.getContentType())){
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT);

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            System.out.println(csvRecords);


        }
    }
    @PostMapping("/createTableViaCSV")
    public void createTableViaCsv(@RequestBody CreateTableViaCsvDTO dto){
        service.createTableViaCsv(dto);

    }

    public void generateInsertSql(){

        //return "INSERT INTO TABLE Values"
    }

    private String generateValuesPart(CSVRecord record){
        return null;
       // return "("+record.getParser().")";
    }

}
