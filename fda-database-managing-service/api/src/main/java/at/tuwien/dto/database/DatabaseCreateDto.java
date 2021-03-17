package at.tuwien.dto.database;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseCreateDto {

    /* container hash */
    private String containerId;

    private String name;

    private String engine;

    private String owner;

    private String creator;

    private String publisher;

    private String publicationYear;

    private String ResourceType;

    private String description;

}
