package at.tuwien.mapper;

import at.tuwien.entities.DatabaseContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class BaseMappingTest {

    private final DatabaseContainerMapper databaseContainerMapper;

    @Autowired
    public BaseMappingTest(DatabaseContainerMapper databaseContainerMapper) {
        this.databaseContainerMapper = databaseContainerMapper;
    }

    @Configuration
    @ComponentScan(basePackages = {"at.tuwien.mapper"})
    public static class BaseMappingContext {
    }

    private static final String CONTAINER_ID = "deadbeef";

    @Test
    public void inspectContainerResponseToDatabaseContainer_succeed() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final InspectContainerResponse data = new InspectContainerResponse();
        final Object instance = data.getClass().getConstructor().newInstance();
        final Field idField = data.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(instance, CONTAINER_ID);
        
        final InspectContainerResponse mockResponse = (InspectContainerResponse) instance;

        final DatabaseContainer container = databaseContainerMapper.inspectContainerResponseToDatabaseContainer(mockResponse);

        assertEquals(CONTAINER_ID, container.getContainerId());
    }

}
