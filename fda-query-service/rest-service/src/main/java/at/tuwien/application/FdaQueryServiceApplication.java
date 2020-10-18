package at.tuwien.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "at.tuwien")
public class FdaQueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaQueryServiceApplication.class, args);
    }

}
