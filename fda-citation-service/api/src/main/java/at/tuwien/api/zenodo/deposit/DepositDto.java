package at.tuwien.api.zenodo.deposit;

import at.tuwien.api.zenodo.files.FileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * TODO add created, modified, embargodate with +00:00 timezone
 */
public class DepositDto {

    private String description;

    @JsonProperty("conceptrecid")
    private Long conceptRecId;

    private Long owner;

    @JsonProperty("access_conditions")
    private String accessConditions;

    private String doi;

    private LinksDto links;

    private MetadataDto metadata;

    @JsonProperty("prereserve_doi")
    private Boolean prereserveDoi;

    private ContributorDto[] contributors;

    private List<FileDto> files;

    private Long id;

    @JsonProperty("record_id")
    private Long recordId;

    private String state;

    private Boolean submitted;

    private String title;

}
