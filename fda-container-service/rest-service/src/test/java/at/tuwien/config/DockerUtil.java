package at.tuwien.config;

import at.tuwien.entities.container.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Log4j2
@Configuration
public class DockerUtil {

    private final DockerClient dockerClient;

    @Autowired
    public DockerUtil(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public void startContainer(Container container) throws InterruptedException {
        final InspectContainerResponse inspect = dockerClient.inspectContainerCmd(container.getHash())
                .exec();
        log.trace("container {} state {}", container.getHash(), inspect.getState().getStatus());
        if (Objects.equals(inspect.getState().getStatus(), "running")) {
            return;
        }
        log.trace("container {} needs to be started", container.getHash());
        dockerClient.startContainerCmd(container.getHash())
                .exec();
        Thread.sleep(12 * 1000L);
        log.debug("container {} was started", container.getHash());
    }

    public void stopContainer(Container container) {
        final InspectContainerResponse inspect = dockerClient.inspectContainerCmd(container.getHash())
                .exec();
        log.trace("container {} state {}", container.getHash(), inspect.getState().getStatus());
        if (!Objects.equals(inspect.getState().getStatus(), "running")) {
            return;
        }
        log.trace("container {} needs to be stopped", container.getHash());
        dockerClient.stopContainerCmd(container.getHash())
                .exec();
        log.debug("container {} was stopped", container.getHash());
    }

    public void removeContainer(Container container) {
        stopContainer(container);
        log.trace("container {} needs to be removed", container.getHash());
        dockerClient.removeContainerCmd(container.getHash())
                .exec();
        log.debug("container {} was removed", container.getHash());
    }

}
