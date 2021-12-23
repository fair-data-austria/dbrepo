package at.tuwien.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SslProperties {

    @Value("${server.ssl.key-alias}")
    public String sslKeyAlias;

    @Value("${server.ssl.key-store}")
    public String sslKeyStore;

    @Value("${server.ssl.key-store-password}")
    public String sslKeyStorePassword;

    @Value("${server.ssl.key-store-type}")
    public String sslKeyStoreType;

}
