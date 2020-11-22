package at.tuwien.service;

import at.tuwien.client.FdaQueryServiceClient;
import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.model.CreateCSVTableWithDataset;
import at.tuwien.model.QueryResult;
import at.tuwien.utils.HistoryTableGenerator;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class TableService {
    private String CREATE_TABLE_STMT = "CREATE TABLE %s (ID SERIAL PRIMARY KEY, %s);";
    private String INSERT_STMT = "INSERT INTO %s (%s) VALUES %s;";
    private String copyInDB = "COPY table_name(city, zipcode) FROM STDIN WITH CSV";

    private HistoryTableGenerator generator;
    private FdaQueryServiceClient client;

    @Autowired
    public TableService(FdaQueryServiceClient client, HistoryTableGenerator generator) {
        this.client = client;
        this.generator = generator;
    }

    public boolean createTableViaCsv(CreateTableViaCsvDTO dto) {
        try {
            //File file = new ClassPathResource("COVID19.csv").getFile();
            File file = new File(dto.getPathToFile());

            String records = "";
            CSVReader csvReader = new CSVReader(new FileReader(file), dto.getDelimiter());
            String[] values = null;
            String[] header = null;
            header = csvReader.readNext();
            //String[] splittedHeader = header[0].split(",");
            String columnNames = Arrays.asList(header).stream().collect(Collectors.joining(","));
            String columnNamesWithDataTypes = Arrays.asList(header).stream().collect(Collectors.joining(" varchar(255), ")) + "  varchar(255)";

//            while ((values = csvReader.readNext()) != null) {
//                //String[] split = values[0].split(",");
//
//                records += "(" + Arrays.asList(values).stream().map(elem -> "'" + elem + "'").collect(Collectors.joining(", ")) + "),";
//
//            }
//            records = records.substring(0, records.length() - 1);

            String tableName = file.getName().replace(".csv", "");

            String createTableStmt = String.format(CREATE_TABLE_STMT, tableName, columnNamesWithDataTypes);


            //String insertIntoTableStmt = String.format(INSERT_STMT, tableName,columnNames, records);

            //Create Table
            client.executeStatement(dto, createTableStmt);
            client.executeStatement(dto, generator.generate(tableName));

            CreateCSVTableWithDataset tableWithDataset = new CreateCSVTableWithDataset();
            tableWithDataset.setColumnNames(columnNames);
            tableWithDataset.setPathToCSVFile(dto.getPathToFile());
            tableWithDataset.setContainerID(dto.getContainerID());
            tableWithDataset.setTableName(tableName);
            tableWithDataset.setDelimiter(dto.getDelimiter());
            //client.executeStatement(dto, insertIntoTableStmt);
            boolean success = client.copyCSVIntoTable(tableWithDataset);
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public QueryResult getListOfTablesForContainerID(String containerID) {
        String listTablesSQL = "SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND " +
                "schemaname != 'information_schema' and tablename NOT like '%_history%' and not tablename='query_store';";

        return client.executeQuery(containerID, listTablesSQL);

    }
}
