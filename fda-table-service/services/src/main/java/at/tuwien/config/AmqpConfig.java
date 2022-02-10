package at.tuwien.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Getter
@Configuration
public class AmqpConfig {

    @Value("${spring.rabbitmq.host}")
    private String ampqHost;

    @Value("${spring.rabbitmq.username}")
    private String amqpUsername;

    @Value("${spring.rabbitmq.password}")
    private String amqpPassword;

    @Bean
    public Channel getChannel() throws IOException, TimeoutException {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ampqHost);
        factory.setUsername(amqpUsername);
        factory.setPassword(amqpPassword);
        log.debug("AMQP host {}, username {}, password {}", ampqHost, amqpUsername, amqpPassword);
        final Connection connection = factory.newConnection();
        return connection.createChannel();
    }

}
