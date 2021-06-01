package at.tuwien.mapper;

import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.model.ExecuteStatementDTO;
import org.springframework.stereotype.Component;

@Component
public class CreateTableViaCsvToExecuteStatementMapper {

    public ExecuteStatementDTO map(CreateTableViaCsvDTO dto, String statement){
        ExecuteStatementDTO statementDTO = new ExecuteStatementDTO();
        statementDTO.setContainerID(dto.getContainerId());
        statementDTO.setStatement(statement);
        return statementDTO;
    }
}
