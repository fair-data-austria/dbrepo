package at.tuwien;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportTableRawQuery {

    private String statement;

    private String filename;

}
