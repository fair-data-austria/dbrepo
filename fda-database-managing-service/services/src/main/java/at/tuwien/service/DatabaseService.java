package at.tuwien.service;

import at.tuwien.clients.FdaContainerManagingClient;

import at.tuwien.dto.CreateDatabaseDTO;
import at.tuwien.model.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    private FdaContainerManagingClient client;

    @Autowired
    public DatabaseService(FdaContainerManagingClient client) {
        this.client = client;
    }

    public boolean createDatabase(CreateDatabaseDTO dto) {
        return client.createDatabaseContainer(dto);
    }

    public List<Database> findAllCreatedDatabases() {
        return client.getCreatedDatabases();
    }

}
