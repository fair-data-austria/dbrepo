package at.tuwien.api.zenodo.files;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {

    private String checksum;

    private String filename;

    private Long filesize;

    private String id;

    private FileLinksDto links;

}
