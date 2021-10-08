package at.tuwien.api.database.deposit;

import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DepositLinksDto {

    private String bucket;

    private String discard;

    private String edit;

    private String files;

    private String html;

    private String latest_draft;

    private String latest_draft_html;

    private String publish;

    private String self;

}
