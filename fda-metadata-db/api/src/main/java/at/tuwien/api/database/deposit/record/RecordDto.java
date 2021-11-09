package at.tuwien.api.database.deposit.record;

import at.tuwien.api.database.deposit.files.RecordFileDto;
import at.tuwien.api.database.deposit.metadata.MetadataDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecordDto {

    @JsonProperty("conceptdoi")
    private String conceptDoi;

    private String doi;

    @JsonProperty("conceptrecid")
    private String recordId;

    @JsonProperty("id")
    private Long refId;

    private RecordFileDto[] files;

    private MetadataDto metadata;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    private Instant created;
}
