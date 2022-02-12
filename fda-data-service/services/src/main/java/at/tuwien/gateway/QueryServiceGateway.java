package at.tuwien.gateway;

import at.tuwien.api.database.table.TableCsvDto;

public interface QueryServiceGateway {

    Integer publish(Long containerId, Long databaseId, Long tableId, TableCsvDto data);
}
