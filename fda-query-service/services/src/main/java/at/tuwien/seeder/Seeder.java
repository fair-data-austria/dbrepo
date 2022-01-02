package at.tuwien.seeder;

import at.tuwien.exception.*;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface Seeder {

    void seed() throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException, IOException;

}
