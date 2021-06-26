package at.tuwien.mapper;

import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.exception.ImageNotSupportedException;
import org.mapstruct.Mapper;

import java.util.Optional;
import java.util.Properties;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    default Properties containerImageToProperties(ContainerImage data) throws ImageNotSupportedException {
        final Properties properties = new Properties();
        final Optional<ContainerImageEnvironmentItem> username = data.getEnvironment()
                .stream()
                .filter(i -> i.getType().equals(ContainerImageEnvironmentItemType.USERNAME))
                .findFirst();
        if (username.isEmpty()) {
            throw new ImageNotSupportedException("Credentials error: no username found");
        }
        final Optional<ContainerImageEnvironmentItem> password = data.getEnvironment()
                .stream()
                .filter(i -> i.getType().equals(ContainerImageEnvironmentItemType.PASSWORD))
                .findFirst();
        if (password.isEmpty()) {
            throw new ImageNotSupportedException("Credentials error: no password found");
        }
        properties.setProperty("user", username.get()
                .getValue());
        properties.setProperty("password", password.get()
                .getValue());
        return properties;
    }

}
