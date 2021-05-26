package at.tuwien.utils;

import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.ImageNotSupportedException;

import java.util.Optional;

public class ContainerDatabaseUtil {

    public static String getUsername(Database database) throws ImageNotSupportedException {
        final Optional<ContainerImageEnvironmentItem> env = database.getContainer()
                .getImage()
                .getEnvironment()
                .stream()
                .filter(i -> i.getKey().equals("POSTGRES_USER"))
                .findFirst();
        if (env.isEmpty()) {
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        return env.get()
                .getValue();
    }

    public static String getPassword(Database database) throws ImageNotSupportedException {
        final Optional<ContainerImageEnvironmentItem> env = database.getContainer()
                .getImage()
                .getEnvironment()
                .stream()
                .filter(i -> i.getKey().equals("POSTGRES_PASSWORD"))
                .findFirst();
        if (env.isEmpty()) {
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        return env.get()
                .getValue();
    }

}
