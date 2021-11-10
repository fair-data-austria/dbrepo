package at.tuwien.config;

import at.tuwien.entities.container.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.util.Objects;

public class DockerConfig {

    private final static DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();

    private final static DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(dockerClientConfig.getDockerHost())
            .sslConfig(dockerClientConfig.getSSLConfig())
            .build();

    public final static HostConfig hostConfig = HostConfig.newHostConfig()
            .withRestartPolicy(RestartPolicy.alwaysRestart());

    public final static DockerClient dockerClient = DockerClientBuilder.getInstance()
            .withDockerHttpClient(dockerHttpClient)
            .build();

    public static void startContainer(Container container) throws InterruptedException {
        final InspectContainerResponse inspect = dockerClient.inspectContainerCmd(container.getHash())
                .exec();
        if (Objects.equals(inspect.getState().getStatus(), "running")) {
            return;
        }
        dockerClient.startContainerCmd(container.getHash())
                .exec();
        Thread.sleep(6 * 1000L);
    }

    public static void stopContainer(Container container) {
        dockerClient.stopContainerCmd(container.getHash())
                .exec();
    }

}
