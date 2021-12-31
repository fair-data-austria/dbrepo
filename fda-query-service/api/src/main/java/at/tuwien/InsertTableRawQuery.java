package at.tuwien;

import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsertTableRawQuery {

    private String query;

    private List<Collection<Object>> values;

}
