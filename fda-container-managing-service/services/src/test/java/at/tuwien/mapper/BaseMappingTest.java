package at.tuwien.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class BaseMappingTest {

    @Configuration
    @ComponentScan(basePackages = {"at.tuwien.mapper"})
    public static class BaseMappingContext {
    }

    @Test
    public void contextLoads() {
        //
    }

}
