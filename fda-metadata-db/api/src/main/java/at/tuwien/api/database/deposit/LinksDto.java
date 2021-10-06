
package at.tuwien.api.database.deposit;

import lombok.*;

import java.net.URI;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinksDto {

    private URI bucket;

    private URI discard;

    private URI edit;

    private URI files;

    private URI html;

    private URI latest_draft;

    private URI latest_draft_html;

    private URI publish;

    private URI self;

}
