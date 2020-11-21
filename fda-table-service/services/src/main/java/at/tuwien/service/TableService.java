package at.tuwien.service;

import at.tuwien.client.FdaQueryServiceClient;
import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.model.QueryResult;
import at.tuwien.utils.HistoryTableGenerator;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class TableService {
    private String CREATE_TABLE_STMT = "CREATE TABLE %s (%s);";
    private String INSERT_STMT = "INSERT INTO %s VALUES %s;";

    private HistoryTableGenerator generator;
    private FdaQueryServiceClient client;

    @Autowired
    public TableService(FdaQueryServiceClient client, HistoryTableGenerator generator) {
        this.client = client;
        this.generator = generator;
    }

    public boolean createTableViaCsv(CreateTableViaCsvDTO dto) {
        try {
            File file = new ClassPathResource("COVID19.csv").getFile();

            String records = "";
            CSVReader csvReader = new CSVReader(new FileReader(file),',');
            String[] values = null;
            String[] header = null;
            header = csvReader.readNext();
            //String[] splittedHeader = header[0].split(",");
            String columnNames = Arrays.asList(header).stream().collect(Collectors.joining(" varchar(255), ")) + "  varchar(255)";

            while ((values = csvReader.readNext()) != null) {
                //String[] split = values[0].split(",");

                records += "(" + Arrays.asList(values).stream().map(elem -> "'" + elem + "'").collect(Collectors.joining(", ")) + "),";

            }
            records = records.substring(0, records.length() - 1);
            String tableName = file.getName().replace(".csv", "");
            String createTableStmt = String.format(CREATE_TABLE_STMT, tableName, columnNames);
            String insertIntoTableStmt = String.format(INSERT_STMT, tableName, records);

            client.executeStatement(dto, createTableStmt);
            client.executeStatement(dto, generator.generate(tableName));
            client.executeStatement(dto, insertIntoTableStmt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public QueryResult getListOfTablesForContainerID(String containerID) {
        String listTablesSQL = "SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND " +
                "schemaname != 'information_schema' and tablename NOT like '%_history%' and not tablename='query_store';";

       return client.executeQuery(containerID,listTablesSQL);

    }
}
