package at.tuwien.config;

import at.tuwien.auth.AuthTokenFilter;
import at.tuwien.gateway.AuthenticationServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationServiceGateway authenticationServiceGateway;

    @Autowired
    public WebSecurityConfig(AuthenticationServiceGateway authenticationServiceGateway) {
        this.authenticationServiceGateway = authenticationServiceGateway;
    }

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter(authenticationServiceGateway);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* enable CORS and disable CSRF */
        http = http.cors().and().csrf().disable();
        /* set session management to stateless */
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        /* set unauthorized requests exception handler */
        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                ).and();
        /* set permissions on endpoints */
        http.authorizeRequests()
                /* our public endpoints */
                .antMatchers(HttpMethod.GET, "/api/container/**/database/data/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/container/**/database/**/table/**/data/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/container/**/database/**/table/**/export/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/container/**/database/query/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/container/**/database/**/query/**").permitAll()
                /* insert endpoint */
                .antMatchers(HttpMethod.POST, "/api/container/**/database/**/table/**/data").permitAll()
                /* our private endpoints */
                .anyRequest().authenticated();
        /* add JWT token filter */
        http.addFilterBefore(authTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
