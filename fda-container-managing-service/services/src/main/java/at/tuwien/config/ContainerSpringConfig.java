package at.tuwien.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.lang.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContainerSpringConfig {

    private final String localDockerHost = SystemUtils.IS_OS_WINDOWS ? "tcp://localhost:2375"
            : "unix:///var/run/docker.sock";

    private final DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(localDockerHost).build();

    @Bean
    public DockerClient dockerClientConfiguration(){
        return DockerClientBuilder.getInstance(config).build();
    }
}
