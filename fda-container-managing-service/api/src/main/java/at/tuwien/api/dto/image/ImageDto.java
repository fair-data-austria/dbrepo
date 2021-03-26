package at.tuwien.api.dto.image;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
public class ImageDto {

    private String repository;

    private String tag;

    private String hash;

    private Instant built;

    private BigInteger size;

    private Integer defaultPort;

    private String[] environment;

    private String architecture;

}
