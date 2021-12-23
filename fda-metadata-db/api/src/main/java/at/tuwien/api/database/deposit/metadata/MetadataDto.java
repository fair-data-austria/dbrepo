package at.tuwien.api.database.deposit.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataDto {

    private String title;

    @JsonProperty("upload_type")
    private UploadTypeDto uploadType;

    @JsonProperty("prereserve_doi")
    private PreserveDoiDto prereserveDoi;

    private String description;

    private String doi;

    @JsonProperty("related_identifiers")
    private IdentifierDto[] relatedIdentifiers;

    @JsonProperty("publication_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date publicationDate;

    @JsonProperty("resource_type")
    private ResourceTypeDto resourceType;

    @JsonProperty("access_right")
    private AccessRightDto accessRight;

    private CreatorDto[] creators;

}
