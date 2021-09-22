package at.tuwien.api.zenodo.files;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileLinksDto {

    private String download;

    private String self;

}
