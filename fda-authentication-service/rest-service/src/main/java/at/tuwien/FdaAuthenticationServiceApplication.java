package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@EnableWebSecurity
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class FdaAuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaAuthenticationServiceApplication.class, args);
    }

}
