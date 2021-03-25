package at.tuwien.service;

import at.tuwien.clients.FdaContainerManagingClient;
import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.model.Database;
import at.tuwien.repository.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    private FdaContainerManagingClient client;

    private final DatabaseRepository databaseRepository;

    @Autowired
    public DatabaseService(FdaContainerManagingClient client, DatabaseRepository databaseRepository) {
        this.client = client;
        this.databaseRepository = databaseRepository;
    }

    public boolean createDatabase(DatabaseCreateDto dto) {
	return false;
    }

    public List<Database> findAllCreatedDatabases() {
        return client.getCreatedDatabases();
    }

}
