package at.tuwien;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsertTableRawQuery {

    private String query;

    private Collection<Object> data;

}
