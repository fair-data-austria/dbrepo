package at.tuwien.utils;


import at.tuwien.client.FdaQueryServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoryTableGenerator {

    private String SYS_PERIOD_COLUMN_STMT = "ALTER TABLE %s ADD COLUMN sys_period tstzrange NOT NULL;";
    private String CREATE_TABLE_HISTORY_STMT = "CREATE TABLE %s_history (LIKE %s);";
    private String CREATE_VERSIONING_TRIGGER_STMT = "CREATE TRIGGER versioning_trigger BEFORE INSERT OR UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', '%s_history', true);";

    private FdaQueryServiceClient client;

    @Autowired
    public HistoryTableGenerator(FdaQueryServiceClient client) {
        this.client = client;
    }

    public String generate(String tableName) {
        String statements = "";
        statements += getSqlStmtForSysPeriodColumn(tableName);
        statements += getSqlStmtForCreateTableHistory(tableName);
        statements += getSqlStmtForVersioningTrigger(tableName);
        return statements;
    }

    private String getSqlStmtForCreateTableHistory(String tableName) {
        String result = String.format(CREATE_TABLE_HISTORY_STMT, tableName, tableName);
        return result;
    }

    private String getSqlStmtForSysPeriodColumn(String tableName) {
        String result = String.format(SYS_PERIOD_COLUMN_STMT, tableName);
        return result;
    }

    private String getSqlStmtForVersioningTrigger(String tableName) {
        String result = String.format(CREATE_VERSIONING_TRIGGER_STMT, tableName, tableName);
        return result;
    }

}
