package at.tuwien.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:fda.properties")
public class FdaProperties {

    @Value("${fda.idp.entity-id}")
    private String entityId;

    @Value("${fda.idp.metadata}")
    private String metadataUrl;

    @Value("${fda.sp.signkey}")
    private String signKeyAlias;

    @Value("${fda.sp.base-url}")
    private String baseUrl;

    @Value("${fda.sp.login.success-url}")
    private String loginSuccessUrl;

    @Value("${fda.sp.login.failure-url}")
    private String loginFailureUrl;

    @Value("${fda.sp.logout.success-url}")
    private String logoutSuccessUrl;

    @Value("${fda.saml.sign-password}")
    private String samlSignPassword;

}