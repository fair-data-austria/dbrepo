package at.tuwien.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;

import java.util.*;

@Configuration
public class SamlConfig extends WebSecurityConfigurerAdapter {

    @Value("${server.ssl.key-store}")
    private String samlKeystoreLocation;

    @Value("${server.ssl.key-alias}")
    private String samlKeystoreAlias;

    @Value("${server.ssl.key-store-password}")
    private String samlKeystorePassword;

    @Bean
    public KeyManager keyManager() {
        final DefaultResourceLoader loader = new DefaultResourceLoader();
        final Resource storeFile = loader.getResource(samlKeystoreLocation);
        final Map<String, String> passwords = new HashMap<>();
        passwords.put(samlKeystoreAlias, samlKeystorePassword);
        passwords.put("saml", samlKeystorePassword);
        return new JKSKeyManager(storeFile, samlKeystorePassword, passwords, samlKeystoreAlias);
    }
}