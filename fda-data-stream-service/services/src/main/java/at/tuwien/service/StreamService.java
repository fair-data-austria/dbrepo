package at.tuwien.service;

import at.tuwien.exception.*;
import org.springframework.stereotype.Service;

@Service
public interface StreamService {

    /**
     * @throws TableMalformedException
     * @throws TableNotFoundException
     * @throws DatabaseNotFoundException
     * @throws ImageNotSupportedException
     */
    void init() throws TableMalformedException, TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException;

}
