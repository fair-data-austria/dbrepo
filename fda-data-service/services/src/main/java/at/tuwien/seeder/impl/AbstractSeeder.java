package at.tuwien.seeder.impl;

import at.tuwien.api.database.query.ImportDto;
import at.tuwien.api.database.query.SaveStatementDto;

public abstract class AbstractSeeder {

    public final static Long CONTAINER_1_ID = 1L;

    public final static Long DATABASE_1_ID = 1L;

    public final static Long TABLE_1_ID = 1L;
    public final static String TABLE_1_LOCATION = "/tmp/traffic.csv";

    public final static Long QUERY_1_ID = 1L;
    public final static String QUERY_1_STATEMENT = "select `linie`,`betriebsdatum`,`fahrzeug`,`fahrt_id` from `timetable_zurich`";

    public final static ImportDto QUERY_1_IMPORT_DTO = ImportDto.builder()
            .location(TABLE_1_LOCATION)
            .build();

    public final static Long QUERY_2_ID = 2L;
    public final static String QUERY_2_STATEMENT = "select `linie`, count(`id`) from `timetable_zurich` group by `linie`";

    public final static SaveStatementDto QUERY_1_SAVE_DTO = SaveStatementDto.builder()
            .statement(QUERY_1_STATEMENT)
            .build();

    public final static SaveStatementDto QUERY_2_SAVE_DTO = SaveStatementDto.builder()
            .statement(QUERY_2_STATEMENT)
            .build();

}
