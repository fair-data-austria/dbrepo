package at.tuwien.seeder.impl;

import at.tuwien.api.database.DatabaseCreateDto;

public abstract class AbstractSeeder {

    public final static Long CONTAINER_1_ID = 1L;

    public final static Long CONTAINER_2_ID = 2L;

    public final static Long DATABASE_1_ID = 1L;
    public final static String DATABASE_1_NAME = "Public Transport in Zürich";
    public final static String DATABASE_1_DESCRIPTION = "Public transport routes and schedules for the city of Zurich https://www.kaggle.com/laa283/zurich-public-transport/version/2";
    public final static Boolean DATABASE_1_PUBLIC = true;

    public final static DatabaseCreateDto DATABASE_1_CREATE_DTO = DatabaseCreateDto.builder()
            .name(DATABASE_1_NAME)
            .isPublic(DATABASE_1_PUBLIC)
            .description(DATABASE_1_DESCRIPTION)
            .build();

    public final static Long DATABASE_2_ID = 2L;
    public final static String DATABASE_2_NAME = "Sensor Data collected from the Server";
    public final static String DATABASE_2_DESCRIPTION = "Collection of various live data from the deployment server";
    public final static Boolean DATABASE_2_PUBLIC = true;

    public final static DatabaseCreateDto DATABASE_2_CREATE_DTO = DatabaseCreateDto.builder()
            .name(DATABASE_2_NAME)
            .isPublic(DATABASE_2_PUBLIC)
            .description(DATABASE_2_DESCRIPTION)
            .build();

}
