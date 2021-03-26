package at.tuwien.api.dto.image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageCreateDto {

    private String repository;

    private String tag;

}
