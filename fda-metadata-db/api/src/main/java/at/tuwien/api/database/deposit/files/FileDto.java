package at.tuwien.api.database.deposit.files;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    private String checksum;

    private String filename;

    private Long filesize;

    private Boolean locked;

    private Long id;

    private FileLinksDto links;

}
