package at.tuwien.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Log4j2
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

    @Value("${fda.sp.success-url}")
    private String successRedirectUrl;

    @Value("${fda.sp.failure-url}")
    private String failureRedirectUrl;

}