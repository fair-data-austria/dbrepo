package at.tuwien.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
public class DatabaseProperties {

    @Value("#{'${fda.db.images}'.split(',')}")
    private List<String> databaseImages;
}
