package at.tuwien.controller;

import at.tuwien.dto.ExecuteInternalQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.model.QueryResult;
import at.tuwien.querystore.TablePojo;
import at.tuwien.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class QueryController {

    private QueryService service;

    @Autowired
    public QueryController(QueryService service) {
        this.service = service;
    }

    @PostMapping("/executeInternalQuery")
    public QueryResult executeInternalQuery(@RequestBody ExecuteInternalQueryDTO dto) {
        return service.executeInternalQuery(dto);
    }

    @PostMapping("/executeStatement")
    public Response executeStatement(@RequestBody ExecuteStatementDTO dto) {
        service.executeStatement(dto);

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PostMapping("/resolvePID")
    public Response resolvePID(@RequestParam int pid) {
        TablePojo tablePojo = service.resolvePID(pid);

        return Response
                .status(Response.Status.OK)
                .entity(tablePojo)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


}
