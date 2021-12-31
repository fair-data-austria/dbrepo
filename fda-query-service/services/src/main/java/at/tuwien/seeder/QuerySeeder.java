package at.tuwien.seeder;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.exception.*;
import at.tuwien.service.impl.StoreServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class QuerySeeder implements Seeder {

    private final static Long TABLE_1_ID = 1L;
    private final static Long DATABASE_1_ID = 1L;

    private final static Long QUERY_1_ID = 1L;
    private final static String QUERY_1_TITLE = "Days with rain in Albury";
    private final static String QUERY_1_DESCRIPTION = "All rain data for Albury";
    private final static String QUERY_1_QUERY = "SELECT date, location, mintemp, maxtemp, rainfall, raintomorrow FROM rain_in_australia WHERE rainfall > 0 ORDER BY date ASC";

    private final static Long TABLE_2_ID = 2L;
    private final static Long DATABASE_2_ID = 2L;

    private final static Long QUERY_2_ID = 2L;
    private final static String QUERY_2_TITLE = "Most COVID-19 cases per population";
    private final static String QUERY_2_DESCRIPTION = "Minimized dataset";
    private final static String QUERY_2_QUERY = "SELECT geoId, countriesandterritories, cumulative_number_for_14_days_of_covid_19_cases_per_100000 FROM infection_covid19 ORDER BY Cumulative_number_for_14_days_of_COVID_19_cases_per_100000 DESC";

    private final static Long QUERY_3_ID = 3L;
    private final static String QUERY_3_TITLE = "Countries with COVID-19 reports";
    private final static String QUERY_3_DESCRIPTION = "Minimized dataset";
    private final static String QUERY_3_QUERY = "SELECT countriesandterritories FROM infection_covid19 GROUP BY countriesandterritories";

    private final static Long TABLE_3_ID = 3L;
    private final static Long DATABASE_3_ID = 3L;

    private final static Long QUERY_4_ID = 4L;
    private final static String QUERY_4_TITLE = "Air quality in Lower Austria";
    private final static String QUERY_4_DESCRIPTION = "Minimized dataset";
    private final static String QUERY_4_QUERY = "SELECT country_code, city, location, coordinates, pollutant, source_name, unit, last_updated, country_label FROM air_quality_at WHERE city = \"Niederösterreich\"";

    private final ExecuteQueryDto QUERY_EXECUTE_1 = ExecuteQueryDto.builder()
            .query(QUERY_1_QUERY)
            .title(QUERY_1_TITLE)
            .build();

    private final ExecuteQueryDto QUERY_EXECUTE_2 = ExecuteQueryDto.builder()
            .query(QUERY_2_QUERY)
            .title(QUERY_2_TITLE)
            .build();

    private final ExecuteQueryDto QUERY_EXECUTE_3 = ExecuteQueryDto.builder()
            .query(QUERY_3_QUERY)
            .title(QUERY_3_TITLE)
            .build();

    private final ExecuteQueryDto QUERY_EXECUTE_4 = ExecuteQueryDto.builder()
            .query(QUERY_4_QUERY)
            .title(QUERY_4_TITLE)
            .build();

    private final StoreServiceImpl queryStoreService;

    @Autowired
    public QuerySeeder(StoreServiceImpl queryStoreService) {
        this.queryStoreService = queryStoreService;
    }

    @Override
    @Transactional
    public void seed() throws QueryStoreException, DatabaseConnectionException, QueryMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, TableNotFoundException {
//        log.debug("seeded query {}", queryStoreService.saveWithoutExecution(DATABASE_1_ID, TABLE_1_ID, QUERY_EXECUTE_1));
//        log.debug("seeded query {}", queryStoreService.saveWithoutExecution(DATABASE_2_ID, TABLE_2_ID, QUERY_EXECUTE_2));
//        log.debug("seeded query {}", queryStoreService.saveWithoutExecution(DATABASE_2_ID, TABLE_2_ID, QUERY_EXECUTE_3));
//        log.debug("seeded query {}", queryStoreService.saveWithoutExecution(DATABASE_3_ID, TABLE_3_ID, QUERY_EXECUTE_4));
    }

}
