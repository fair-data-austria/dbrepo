package at.tuwien.service;

import at.tuwien.clients.FdaContainerManagingClient;
import at.tuwien.clients.ResponseException;
import at.tuwien.dto.CreateDatabaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;

@Service
public class DatabaseService {
    @Autowired
    private FdaContainerManagingClient client;

    public boolean createDatabase(CreateDatabaseDTO dto) {
        return client.createDatabaseContainer(dto);
    }
}
