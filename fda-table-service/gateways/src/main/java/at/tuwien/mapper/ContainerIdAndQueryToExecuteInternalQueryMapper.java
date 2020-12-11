package at.tuwien.mapper;

import at.tuwien.model.ExecuteInternalQueryDTO;

public class ContainerIdAndQueryToExecuteInternalQueryMapper {

    public ExecuteInternalQueryDTO map(String containerID, String query){
        ExecuteInternalQueryDTO dto = new ExecuteInternalQueryDTO();
        dto.setContainerID(containerID);
        dto.setQuery(query);
        return dto;
    }
}
