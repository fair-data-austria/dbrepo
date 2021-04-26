package at.tuwien.utils;

import at.tuwien.dto.ExecuteQueryDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ReWrittenQueryWrapper.class)
public class ReWrittenQueryWrapperTest {

    @Disabled
    @Test
    public void test_determineReWrittenQuery() {
        ReWrittenQueryWrapper wrapper = new ReWrittenQueryWrapper();
        ExecuteQueryDTO dto = new ExecuteQueryDTO();
        dto.setQuery("SELECT * FROM TABLEA WHERE TABLEA.ID = '2'");


        Assertions.assertEquals(wrapper.determineReWrittenQuery(dto), "SELECT * FROM TABLEA JOIN TABLEA_history where lower(sys_period) <= '2020-11-28 23:41:58.445' AND '2020-11-28 23:41:58.445' < upper(sys_period) OR upper(sys_period) IS NULL JOIN TABLEB JOIN TABLEB_history where " +
                "lower(sys_period) <= '2020-11-28 23:42:04.84' AND '2020-11-28 23:42:04.84' < " +
                "upper(sys_period) OR upper(sys_period) IS NULL ON TABLEA.a = TABLEB.b WHERE A = 2");

    }

}