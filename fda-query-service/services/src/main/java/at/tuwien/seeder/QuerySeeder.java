package at.tuwien.seeder;

import at.tuwien.api.database.query.SaveStatementDto;
import at.tuwien.exception.*;
import at.tuwien.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class QuerySeeder implements Seeder {

    private final StoreService storeService;

    private final static Long DATABASE_1_ID = 1L;

    private final static String QUERY_1_QUERY = "SELECT date, location, mintemp, maxtemp, rainfall, raintomorrow FROM rain_in_australia WHERE rainfall > 0 ORDER BY date ASC";

    private final static String QUERY_2_QUERY = "SELECT geoId, countriesandterritories, cumulative_number_for_14_days_of_covid_19_cases_per_100000 FROM infection_covid19 ORDER BY Cumulative_number_for_14_days_of_COVID_19_cases_per_100000 DESC";

    private final static String QUERY_3_QUERY = "SELECT countriesandterritories FROM infection_covid19 GROUP BY countriesandterritories";

    private final static String QUERY_4_QUERY = "SELECT country_code, city, location, coordinates, pollutant, source_name, unit, last_updated, country_label FROM air_quality_at WHERE city = \"Niederösterreich\"";

    private final SaveStatementDto QUERY_EXECUTE_1 = SaveStatementDto.builder()
            .statement(QUERY_1_QUERY)
            .build();

    private final SaveStatementDto QUERY_EXECUTE_2 = SaveStatementDto.builder()
            .statement(QUERY_2_QUERY)
            .build();

    private final SaveStatementDto QUERY_EXECUTE_3 = SaveStatementDto.builder()
            .statement(QUERY_3_QUERY)
            .build();

    private final SaveStatementDto QUERY_EXECUTE_4 = SaveStatementDto.builder()
            .statement(QUERY_4_QUERY)
            .build();

    @Autowired
    public QuerySeeder(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    @Transactional
    public void seed() throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {
        log.debug("seeded query {}", storeService.insert(DATABASE_1_ID, null, QUERY_EXECUTE_1));
        log.debug("seeded query {}", storeService.insert(DATABASE_1_ID, null, QUERY_EXECUTE_2));
        log.debug("seeded query {}", storeService.insert(DATABASE_1_ID, null, QUERY_EXECUTE_3));
        log.debug("seeded query {}", storeService.insert(DATABASE_1_ID, null, QUERY_EXECUTE_4));
    }

}
