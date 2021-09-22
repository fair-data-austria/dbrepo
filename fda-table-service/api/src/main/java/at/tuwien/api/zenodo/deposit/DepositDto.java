package at.tuwien.api.zenodo.deposit;

import at.tuwien.api.zenodo.MetadataDto;
import at.tuwien.api.zenodo.files.FileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositDto {

    @JsonProperty("conceptrecid")
    private Long conceptRecId;

    private Instant created;

    private List<FileDto> files;

    private Long id;

    private Instant modified;

    private Long owner;

    private MetadataDto metadata;

    @JsonProperty("record_id")
    private Long recordId;

    private String state;

    private Boolean submitted;

    private String title;

}
