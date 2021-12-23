package at.tuwien.seeder;

import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.exception.AmqpException;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DatabaseMalformedException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.service.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class DatabaseSeeder implements Seeder {

    private final static Long DATABASE_1_ID = 1L;
    private final static String DATABASE_1_NAME = "Rain in Australia";
    private final static String DATABASE_1_DESCRIPTION = "This dataset contains about 10 years of daily weather observations from many locations across Australia. RainTomorrow is the target variable to predict. It means -- did it rain the next day, Yes or No? This column is Yes if the rain for that day was 1mm or more.";
    private final static Boolean DATABASE_1_PUBLIC = false;


    private final static Long DATABASE_2_ID = 2L;
    private final static String DATABASE_2_NAME = "Novel Coronavirus (COVID19) Cases Data";
    private final static String DATABASE_2_DESCRIPTION = "Novel Corona Virus (COVID-19) epidemiological data since 22 January 2020. The data is compiled by the Johns Hopkins University Center for Systems Science and Engineering (JHU CCSE) from various sources including the World Health Organization (WHO), DXY.cn, BNO News, National Health Commission of the People’s Republic of China (NHC), China CDC (CCDC), Hong Kong Department of Health, Macau Government, Taiwan CDC, US CDC, Government of Canada, Australia Government Department of Health, European Centre for Disease Prevention and Control (ECDC), Ministry of Health Singapore (MOH), and others. JHU CCSE maintains the data on the 2019 Novel Coronavirus COVID-19 (2019-nCoV) Data Repository on Github.";
    private final static Boolean DATABASE_2_PUBLIC = false;

    private final static Long DATABASE_3_ID = 3L;
    private final static String DATABASE_3_NAME = "Air Quality in Lower Austria 2019 to 2021";
    private final static String DATABASE_3_DESCRIPTION = "Openair quality measurements between 2019-01-01 and 2021-01-01 in Wiener Neustadt, Lower Austria, Austria with a low-cost sensor.";
    private final static Boolean DATABASE_3_PUBLIC = false;

    private final static DatabaseCreateDto DATABASE_1_CREATE = DatabaseCreateDto.builder()
            .containerId(DATABASE_1_ID)
            .description(DATABASE_1_DESCRIPTION)
            .isPublic(DATABASE_1_PUBLIC)
            .name(DATABASE_1_NAME)
            .build();

    private final static DatabaseCreateDto DATABASE_2_CREATE = DatabaseCreateDto.builder()
            .containerId(DATABASE_2_ID)
            .description(DATABASE_2_DESCRIPTION)
            .isPublic(DATABASE_2_PUBLIC)
            .name(DATABASE_2_NAME)
            .build();

    private final static DatabaseCreateDto DATABASE_3_CREATE = DatabaseCreateDto.builder()
            .containerId(DATABASE_3_ID)
            .description(DATABASE_3_DESCRIPTION)
            .isPublic(DATABASE_3_PUBLIC)
            .name(DATABASE_3_NAME)
            .build();

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseSeeder(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void seed() throws ImageNotSupportedException, AmqpException, ContainerNotFoundException,
            DatabaseMalformedException {
        log.debug("seeded database {}", databaseService.create(DATABASE_1_CREATE));
        log.debug("seeded database {}", databaseService.create(DATABASE_2_CREATE));
        log.debug("seeded database {}", databaseService.create(DATABASE_3_CREATE));
    }

}
