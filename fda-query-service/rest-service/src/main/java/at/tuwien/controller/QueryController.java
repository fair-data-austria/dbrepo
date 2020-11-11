package at.tuwien.controller;

import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.dto.QueryDatabaseDTO;
import at.tuwien.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QueryController {

    private QueryService service;

    @Autowired
    public QueryController(QueryService service) {
        this.service = service;
    }

    @PostMapping("/executeQuery")
    public Response executeQuery(@RequestBody QueryDatabaseDTO dto) {
        List<Map<String, Object>> rs = null;
        try {
            rs = service.queryDatabase(dto);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Response
                .status(Response.Status.OK)
                .entity(rs)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PostMapping("/executeStatement")
    public Response executeStatement(@RequestBody ExecuteStatementDTO dto) {
        List<Map<String, Object>> rs = null;
        service.executeStatement(dto);

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


}
