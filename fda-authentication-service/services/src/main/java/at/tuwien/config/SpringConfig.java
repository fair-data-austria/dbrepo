package at.tuwien.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

import java.io.IOException;

@Log4j2
@Configuration
public class SpringConfig extends WebSecurityConfigurerAdapter {

    private final FilterChainProxy samlFilter;
    private final SAMLEntryPoint samlEntryPoint;
    private final MetadataGeneratorFilter metadataGeneratorFilter;

    @Autowired
    public SpringConfig(FilterChainProxy samlFilter, SAMLEntryPoint samlEntryPoint,
                        MetadataGeneratorFilter metadataGeneratorFilter) {
        this.samlFilter = samlFilter;
        this.samlEntryPoint = samlEntryPoint;
        this.metadataGeneratorFilter = metadataGeneratorFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .httpBasic()
                .authenticationEntryPoint(samlEntryPoint)
                .and()
                .addFilterBefore(metadataGeneratorFilter, ChannelProcessingFilter.class)
                .addFilterAfter(samlFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(samlFilter, CsrfFilter.class)
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .addLogoutHandler((request, response, authentication) -> {
                    try {
                        response.sendRedirect("/saml/logout");
                    } catch (IOException e) {
                        log.error("Logout failed");
                        log.throwing(e);
                    }
                });
    }
}
