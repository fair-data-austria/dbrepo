package at.tuwien.utils;

import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;

@Log4j2
public class TableUtils {

    public static List<String> genericHeaders(int end) {
        final List<String> list = new LinkedList<>();
        for (int i = 0; i < end; i++) {
            list.add("col_" + i);
        }
        return list;
    }

}
