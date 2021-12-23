package at.tuwien.api.database.deposit.files;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileLinksDto {

    private String download;

    private String self;

}
