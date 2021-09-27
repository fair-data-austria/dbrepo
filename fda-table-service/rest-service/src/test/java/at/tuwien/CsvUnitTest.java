package at.tuwien;

import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;

public abstract class CsvUnitTest {

    public final static ColumnCreateDto[] COLUMNS_CSV02 = new ColumnCreateDto[]{
            ColumnCreateDto.builder()
                    .type(ColumnTypeDto.NUMBER)
                    .name("id")
                    .nullAllowed(false)
                    .primaryKey(true)
                    .unique(true)
                    .build(),
            ColumnCreateDto.builder()
                    .type(ColumnTypeDto.STRING)
                    .name("nouniquestr")
                    .nullAllowed(false)
                    .primaryKey(false)
                    .unique(false)
                    .build(),
            ColumnCreateDto.builder()
                    .type(ColumnTypeDto.ENUM)
                    .name("method")
                    .nullAllowed(false)
                    .primaryKey(false)
                    .unique(false)
                    .enumValues(new String[] {"mk", "zf", "ac", "em"})
                    .build(),
            ColumnCreateDto.builder()
                    .type(ColumnTypeDto.STRING)
                    .name("company")
                    .nullAllowed(false)
                    .primaryKey(false)
                    .unique(false)
                    .build(),
            ColumnCreateDto.builder()
                    .type(ColumnTypeDto.NUMBER)
                    .name("measurements")
                    .nullAllowed(false)
                    .primaryKey(false)
                    .unique(false)
                    .build(),
            ColumnCreateDto.builder()
                    .type(ColumnTypeDto.NUMBER)
                    .name("trialn")
                    .nullAllowed(false)
                    .primaryKey(false)
                    .unique(false)
                    .build(),};

}
