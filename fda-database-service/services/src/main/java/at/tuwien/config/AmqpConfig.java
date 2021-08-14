package at.tuwien.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Log4j2
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

    @Bean
    public DeliverCallback deliverCallback() {
        return ((consumerTag, payload) -> {
            log.debug("recceived message {}", payload);
        });
    }

}
