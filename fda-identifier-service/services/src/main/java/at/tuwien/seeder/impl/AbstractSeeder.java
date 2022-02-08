package at.tuwien.seeder.impl;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;

public abstract class AbstractSeeder {

    public final static Long CONTAINER_1_ID = 1L;

    public final static Long DATABASE_1_ID = 1L;

    public final static Long TABLE_1_ID = 1L;

    public final static Long QUERY_1_ID = 1L;

    public final static Long IDENTIFIER_1_ID = 1L;
    public final static Long IDENTIFIER_1_QUERY_ID = QUERY_1_ID;
    public final static String IDENTIFIER_1_DESCRIPTION = "Query that select the line, date, ride and ride id for 2017";
    public final static String IDENTIFIER_1_TITLE = "Lines with Ride ID";
    public final static VisibilityTypeDto IDENTIFIER_1_VISIBILITY = VisibilityTypeDto.SELF;

    public final static IdentifierDto IDENTIFIER_1_CREATE_DTO = IdentifierDto.builder()
            .dbid(DATABASE_1_ID)
            .cid(CONTAINER_1_ID)
            .description(IDENTIFIER_1_DESCRIPTION)
            .title(IDENTIFIER_1_TITLE)
            .qid(IDENTIFIER_1_QUERY_ID)
            .visibility(IDENTIFIER_1_VISIBILITY)
            .build();

}
