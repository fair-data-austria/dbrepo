package at.tuwien.controller;

import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.model.QueryResult;
import at.tuwien.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class TableController {

    private TableService service;

    @Autowired
    public TableController(TableService service){
        this.service = service;
    }

    @PostMapping("/createTableViaCSV")
    public void createTableViaCsv(@RequestBody CreateTableViaCsvDTO dto){
        service.createTableViaCsv(dto);

    }

    @GetMapping("/listTables")
    public Response listTables(@RequestParam String containerID){
        QueryResult result =service.getListOfTablesForContainerID(containerID);

        if(result != null){
            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(result)
                    .build();
        }
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


}
