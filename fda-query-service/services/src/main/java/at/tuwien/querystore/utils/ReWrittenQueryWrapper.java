package at.tuwien.querystore.utils;

import at.tuwien.dto.ExecuteQueryDTO;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public class ReWrittenQueryWrapper {

    private String reWrittenQueryStatement = "%s JOIN %s_history where lower(sys_period) <= '%s' AND '%s' < upper(sys_period) OR upper(sys_period) IS NULL";

    public String determineReWrittenQuery(ExecuteQueryDTO dto) {
        String query = dto.getQuery();
        try {
            Statement statement = CCJSqlParserUtil.parse(dto.getQuery());
            if (statement instanceof Select) {
                Select select = (Select) statement;
                TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
                List<String> tableNames = tablesNamesFinder.getTableList(select);
                for (String tableName : tableNames) {
                    String s = generateViewForTable(tableName);
                    query = query.replaceFirst(tableName,s);
                }
            }

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return query;
    }

    private String generateViewForTable(String tableName) {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return String.format(reWrittenQueryStatement, tableName, tableName,timestamp,timestamp);
    }

}
