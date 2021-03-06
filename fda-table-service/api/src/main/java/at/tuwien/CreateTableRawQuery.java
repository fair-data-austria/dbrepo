package at.tuwien;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTableRawQuery {

    private String query;

    /**
     * True if the "id" column was autogenerated by the service (e.g. not present before)
     */
    private Boolean generated;

}
