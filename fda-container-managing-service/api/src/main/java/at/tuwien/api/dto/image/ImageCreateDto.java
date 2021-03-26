package at.tuwien.api.dto.image;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ImageCreateDto {

    @NotNull
    private String repository;

    @NotNull
    private String tag;

    @NotNull
    private Integer defaultPort;

    @NotNull
    private String[] environment;

    public String toCompact() {
        return repository + ":" + (tag.isEmpty() ? "latest" : tag);
    }

}
