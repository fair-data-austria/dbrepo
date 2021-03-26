package at.tuwien.api.dto.image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageBriefDto {

    private Long id;

    private String repository;

    private String tag;

}
