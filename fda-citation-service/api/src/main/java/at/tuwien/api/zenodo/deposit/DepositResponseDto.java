package at.tuwien.api.zenodo.deposit;

import at.tuwien.api.zenodo.files.FileResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositResponseDto {

    /**
     * {@link Instant} without timezone seems broken
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime modified;

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

    private CreatorDto[] contributors;

    private List<FileResponseDto> files;

    private Long id;

    @JsonProperty("record_id")
    private Long recordId;

    private String state;

    private Boolean submitted;

    private String title;

}