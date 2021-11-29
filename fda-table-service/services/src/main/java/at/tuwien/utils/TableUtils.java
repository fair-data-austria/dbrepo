package at.tuwien.utils;

import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumnType;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.nonNull;

@Log4j2
public class TableUtils {

    public static List<String> fill(int start, int end) {
        final List<String> list = new LinkedList<>();
        for (int i = start; i < end; i++) {
            list.add("col_" + i);
        }
        return list;
    }

    public static boolean needsPrimaryKey(Table table) {
        return table.getColumns()
                .stream()
                .filter(c -> nonNull(c.getAutoGenerated()))
                .anyMatch(c -> c.getAutoGenerated() && c.getColumnType().equals(TableColumnType.NUMBER));
    }

}