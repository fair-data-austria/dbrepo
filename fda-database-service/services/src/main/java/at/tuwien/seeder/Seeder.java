package at.tuwien.seeder;

import at.tuwien.exception.AmqpException;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DatabaseMalformedException;
import at.tuwien.exception.ImageNotSupportedException;
import org.springframework.stereotype.Component;

@Component
public interface Seeder {

    void seed() throws ImageNotSupportedException, AmqpException, ContainerNotFoundException, DatabaseMalformedException;

}
