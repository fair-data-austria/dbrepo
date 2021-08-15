package at.tuwien.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Getter
@Configuration
public class AmqpConfig {

    @Value("${spring.rabbitmq.host}")
    private String ampqHost;

    @Bean
    public Channel getChannel() throws IOException, TimeoutException {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ampqHost);
        final Connection connection = factory.newConnection();
        return connection.createChannel();
    }

}
