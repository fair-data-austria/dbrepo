package at.tuwien.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Bean
    public HostConfig hostConfig() {
        return HostConfig.newHostConfig()
                .withRestartPolicy(RestartPolicy.alwaysRestart());
    }

    @Bean
    public DockerClient dockerClientConfiguration() {
        final DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();
        return DockerClientBuilder.getInstance(config)
                .build();
    }
}
