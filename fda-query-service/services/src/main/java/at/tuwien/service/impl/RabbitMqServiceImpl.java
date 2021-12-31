package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.service.MessageQueueService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.ConnectException;

@Log4j2
@Service
public class RabbitMqServiceImpl implements MessageQueueService {



    @Override
    @Transactional
    public void createUserConsumer(Table table) throws IOException {
        channel.basicConsume(table.getTopic(), true, (consumerTag, response) -> {
            try {
                dataService.insert(table.getDatabase().getId(), table.getId(), objectMapper.readValue(response.getBody(), TableCsvDto.class));
            } catch (JsonParseException | MismatchedInputException e) {
                log.warn("Could not parse AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (ConnectException e) {
                log.warn("Could not redirect AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (ImageNotSupportedException | TableMalformedException
                    | DatabaseNotFoundException | TableNotFoundException e) {
                log.warn("Could not insert AMQP payload {}", e.getMessage());
                if (e.getCause() instanceof DatabaseNotFoundException) {
                    log.info("Database id {} not found", table.getDatabase().getId());
                }
                if (e.getCause() instanceof TableNotFoundException) {
                    log.info("Table id {} not found", table.getDatabase().getId());
                }
                /* ignore */
            }
        }, consumerTag -> {/* */});
        log.debug("declare consumer {}", table);
    }

}
