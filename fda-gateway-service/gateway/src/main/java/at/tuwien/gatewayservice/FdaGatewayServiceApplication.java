package at.tuwien.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "at.tuwien")
public class FdaGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaGatewayServiceApplication.class, args);
    }

}
