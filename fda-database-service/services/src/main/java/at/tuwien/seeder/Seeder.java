package at.tuwien.seeder;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public interface Seeder {

    @EventListener(ApplicationReadyEvent.class)
    void seed();
}
