package at.tuwien.seeder.impl;

import at.tuwien.api.database.query.SaveStatementDto;

public abstract class AbstractSeeder {

    public final static Long CONTAINER_1_ID = 1L;

    public final static Long DATABASE_1_ID = 1L;

    public final static Long TABLE_1_ID = 1L;

    public final static Long QUERY_1_ID = 1L;
    public final static String QUERY_1_STATEMENT = "select `linie`,`betriebsdatum`,`fahrzeug`,`fahrt_id` from `fahrzeiten_soll-ist_2017`";

    public final static SaveStatementDto QUERY_1_SAVE_DTO = SaveStatementDto.builder()
            .statement(QUERY_1_STATEMENT)
            .build();

}
