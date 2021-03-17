package at.tuwien.endpoints;

import at.tuwien.dto.database.DatabaseChangeDto;
import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.dto.table.TableBriefDto;
import at.tuwien.dto.table.TableDto;
import at.tuwien.model.Database;
import at.tuwien.service.DatabaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/database/{id}")
public class TableController {

//    private final DatabaseService databaseService;
//
//    @Autowired
//    public TableController(DatabaseService databaseService) {
//        this.databaseService = databaseService;
//    }
//
//    @GetMapping("/table")
//    @ApiOperation(value = "List all database tables")
//    public ResponseEntity<List<TableBriefDto>> findAll(@RequestParam String id) {
//        return null;
//    }
//
//    @PostMapping("/table")
//    @ApiOperation(value = "Creates a new database table", notes = "Creates a new database table in a container")
//    public ResponseEntity create(@RequestParam String id, @RequestBody DatabaseCreateDto dto) {
////        LOGGER.debug("creating new database");
////        boolean succeed = service.createDatabase(dto);
////        if (succeed) {
////            return Response
////                    .status(Response.Status.CREATED)
////                    .entity("Database container successfully created and started!")
////                    .type(MediaType.APPLICATION_JSON)
////                    .build();
////        }
////        return Response
////                .status(Response.Status.INTERNAL_SERVER_ERROR)
////                .type(MediaType.APPLICATION_JSON)
////                .build();
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .build();
//    }
//
//    @GetMapping("/table/{tid}")
//    @ApiOperation(value = "Get all informations about a database table")
//    public ResponseEntity<TableDto> findById(@RequestParam String id, @RequestParam String tid) {
//        return null;
//    }
//
//    @PutMapping("/table/{tid}")
//    @ApiOperation(value = "Change the state of a database table")
//    public ResponseEntity<?> modify(@RequestParam String id, @RequestParam String tid, @RequestBody DatabaseChangeDto changeDto) {
//        return ResponseEntity.status(HttpStatus.ACCEPTED)
//                .build();
//    }
//
//    @DeleteMapping("/table/{tid}")
//    @ApiOperation(value = "Delete a database table")
//    public ResponseEntity delete(@RequestParam String id, @RequestParam String tid) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .build();
//    }

}
