package at.tuwien.config;

import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.util.VelocityFactory;

import java.util.ArrayList;

@Configuration
public class ProcessingConfig {

    @Bean
    public HTTPPostBinding httpPostBinding(StaticBasicParserPool parserPool) {
        return new HTTPPostBinding(parserPool, VelocityFactory.getEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding(StaticBasicParserPool parserPool) {
        return new HTTPRedirectDeflateBinding(parserPool);
    }

    @Bean
    public SAMLProcessorImpl processor(StaticBasicParserPool parserPool) {
        ArrayList<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpRedirectDeflateBinding(parserPool));
        bindings.add(httpPostBinding(parserPool));
        return new SAMLProcessorImpl(bindings);
    }


}
