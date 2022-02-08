package at.tuwien.seeder.impl;

import at.tuwien.querystore.Query;
import at.tuwien.seeder.Seeder;
import at.tuwien.service.QueryService;
import at.tuwien.service.StoreService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuerySeederImpl extends AbstractSeeder implements Seeder {

    private final StoreService storeService;
    private final QueryService queryService;

    @Autowired
    public QuerySeederImpl(StoreService storeService, QueryService queryService) {
        this.storeService = storeService;
        this.queryService = queryService;
    }

    @SneakyThrows
    @Override
    public void seed() {
        if (storeService.findAll(CONTAINER_1_ID, DATABASE_1_ID).stream().anyMatch(q -> q.getId().equals(QUERY_1_ID))) {
            log.warn("Already seeded. Skip.");
            return;
        }
        final Query query1 = storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, null, QUERY_1_SAVE_DTO);
        log.info("Saved query id {}", query1.getId());
    }

}
