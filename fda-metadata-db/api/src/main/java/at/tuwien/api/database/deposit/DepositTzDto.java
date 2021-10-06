package at.tuwien.api.database.deposit;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.api.database.deposit.metadata.CreatorDto;
import at.tuwien.api.database.deposit.metadata.MetadataDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositTzDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    private Instant created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    private Instant modified;

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

    private CreatorDto[] contributors;

    private List<FileDto> files;

    private Long id;

    @JsonProperty("record_id")
    private Long recordId;

    private String state;

    private Boolean submitted;

    private String title;

}