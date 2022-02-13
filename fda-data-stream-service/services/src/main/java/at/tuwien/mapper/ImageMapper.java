package at.tuwien.mapper;

import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.exception.ImageNotSupportedException;
import org.mapstruct.Mapper;

import java.util.Objects;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageMapper.class);

    default String containerImageToUsername(ContainerImage data) throws ImageNotSupportedException {
        final Optional<ContainerImageEnvironmentItem> username = data.getEnvironment()
                .stream()
                .filter(e -> e.getType().equals(ContainerImageEnvironmentItemType.PRIVILEGED_USERNAME))
                .findFirst();
        if (username.isEmpty()) {
            log.error("Failed to obtain privileged user for insert");
            throw new ImageNotSupportedException("Failed to obtain privileged user");
        }
        return username.get()
                .getValue();
    }

    default String containerImageToPassword(ContainerImage data) throws ImageNotSupportedException {
        final Optional<ContainerImageEnvironmentItem> username = data.getEnvironment()
                .stream()
                .filter(e -> e.getType().equals(ContainerImageEnvironmentItemType.PRIVILEGED_PASSWORD))
                .findFirst();
        if (username.isEmpty()) {
            log.error("Failed to obtain privileged user for insert");
            throw new ImageNotSupportedException("Failed to obtain privileged user");
        }
        return username.get()
                .getValue();
    }

}
