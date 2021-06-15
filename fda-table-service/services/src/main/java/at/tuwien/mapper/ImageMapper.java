package at.tuwien.mapper;

import at.tuwien.entities.container.image.ContainerImage;
import org.mapstruct.Mapper;

import java.util.Properties;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    default Properties containerImageToProperties(ContainerImage data) {
        final Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");
        return properties;
    }

}
