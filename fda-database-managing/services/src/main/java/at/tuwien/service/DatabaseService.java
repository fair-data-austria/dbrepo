package at.tuwien.service;

import at.tuwien.clients.FdaContainerManagingClient;
import at.tuwien.dto.CreateDatabaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {
    @Autowired
    private FdaContainerManagingClient client;

    public void createDatabase(CreateDatabaseDTO dto) {
        client.createDatabaseContainer(dto);
    }
}
