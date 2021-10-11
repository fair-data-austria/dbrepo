package at.tuwien.config;

import at.tuwien.service.UserService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.*;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.*;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.*;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.*;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.File;
import java.util.*;

/**
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SamlConfig extends WebSecurityConfigurerAdapter implements InitializingBean, DisposableBean {

    private final UserService userService;

    private Timer backgroundTaskTimer;
    private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;

    @Autowired
    public SamlConfig(UserService userService) {
        this.userService = userService;
    }

    @Value("${fda.saml.keystore.location}")
    private String samlKeystoreLocation;

    @Value("${fda.saml.keystore.alias}")
    private String samlKeystoreAlias;

    @Value("${fda.saml.keystore.password}")
    private String samlKeystorePassword;

    @Value("${fda.identity.provider.metadata}")
    private String identityProviderMetadataPath;

    @Value("${fda.identity.provider.discovery.url}")
    private String identityProviderDiscoveryUrl;

    @Value("${fda.identity.provider.discovery.response}")
    private String identityProviderDiscoveryResponseUrl;

    /* The filter is waiting for connections on URL suffixed with filterSuffix and presents SP metadata there */
    @Bean
    public MetadataDisplayFilter metadataDisplayFilter() {
        return new MetadataDisplayFilter();
    }

    /* Handler deciding where to redirect user after successful login */
    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
        SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        successRedirectHandler.setDefaultTargetUrl("/landing");
        return successRedirectHandler;
    }

    /* Handler deciding where to redirect user after failed login */
    @Bean
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler failureHandler =
                new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setUseForward(true);
        failureHandler.setDefaultFailureUrl("/error");
        return failureHandler;
    }

    @Bean
    public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
        SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter = new SAMLWebSSOHoKProcessingFilter();
        samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
        samlWebSSOHoKProcessingFilter.setAuthenticationManager(authenticationManager());
        samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return samlWebSSOHoKProcessingFilter;
    }

    /* Processing filter for WebSSO profile messages */
    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return samlWebSSOProcessingFilter;
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
        return new MetadataGeneratorFilter(metadataGenerator());
    }

    /* Handler for successful logout */
    @Bean
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
        SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
        successLogoutHandler.setDefaultTargetUrl("/");
        return successLogoutHandler;
    }

    /* Logout handler terminating local session */
    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler logoutHandler =
                new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.setClearAuthentication(true);
        return logoutHandler;
    }

    /* Filter processing incoming logout messages. First argument determines URL user will be redirected to after
    successful global logout */
    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(successLogoutHandler(),
                logoutHandler());
    }

    /* Overrides default logout processing filter with the one processing SAML messages */
    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
        return new SAMLLogoutFilter(successLogoutHandler(),
                new LogoutHandler[]{logoutHandler()},
                new LogoutHandler[]{logoutHandler()});
    }

    /* Bindings */
    private ArtifactResolutionProfile artifactResolutionProfile() {
        final ArtifactResolutionProfileImpl artifactResolutionProfile =
                new ArtifactResolutionProfileImpl(httpClient());
        artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
        return artifactResolutionProfile;
    }

    @Bean
    public HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
        return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
    }

    @Bean
    public HTTPSOAP11Binding soapBinding() {
        return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
        return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
        return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    public HTTPSOAP11Binding httpSOAP11Binding() {
        return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPAOS11Binding httpPAOS11Binding() {
        return new HTTPPAOS11Binding(parserPool());
    }

    /* Processor */
    @Bean
    public SAMLProcessorImpl processor() {
        Collection<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpRedirectDeflateBinding());
        bindings.add(httpPostBinding());
        bindings.add(artifactBinding(parserPool(), velocityEngine()));
        bindings.add(httpSOAP11Binding());
        bindings.add(httpPAOS11Binding());
        return new SAMLProcessorImpl(bindings);
    }

    /**
     * Define the security filter chain in order to support SSO Auth by using SAML 2.0
     *
     * @return Filter chain proxy
     * @throws Exception
     */
    @Bean
    public FilterChainProxy samlFilter() throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/context/saml/login/**"),
                samlEntryPoint()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/context/saml/logout/**"),
                samlLogoutFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/context/saml/metadata/**"),
                metadataDisplayFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/context/saml/SSO/**"),
                samlWebSSOProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/context/saml/SSOHoK/**"),
                samlWebSSOHoKProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/context/saml/SingleLogout/**"),
                samlLogoutProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/context/saml/discovery/**"),
                samlDiscovery()));
        return new FilterChainProxy(chains);
    }

    /**
     * Returns the authentication manager currently used by Spring.
     * It represents a bean definition with the aim allow wiring from
     * other classes performing the Inversion of Control (IoC).
     *
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Defines the web based security configuration.
     *
     * @param http It allows configuring web based security for specific http requests.
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(samlEntryPoint());
        http.addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
                .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(samlFilter(), CsrfFilter.class);
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/saml/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .anyRequest().authenticated();
        http.logout()
                .disable();    // The logout procedure is already handled by SAML filters.
    }

    /**
     * Sets a custom authentication provider.
     *
     * @param auth SecurityBuilder used to create an AuthenticationManager.
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(samlAuthenticationProvider());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    /* Filter automatically generates default SP metadata */
    @Bean
    public MetadataGenerator metadataGenerator() {
        final MetadataGenerator metadataGenerator = new MetadataGenerator();
        metadataGenerator.setEntityId("at:tuwien:dbrepo:auth");
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager());
        return metadataGenerator;
    }

    /* IDP Metadata configuration - paths to metadata of IDPs in circle of trust is here. Do not forget to call
        inititalize method on providers */
    @Bean
    @Qualifier("metadata")
    public CachingMetadataManager metadata() throws MetadataProviderException {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(ssoCircleExtendedMetadataProvider());
        return new CachingMetadataManager(providers);
    }

    @Bean
    @Qualifier("idp-ssocircle")
    public ExtendedMetadataDelegate ssoCircleExtendedMetadataProvider() throws MetadataProviderException {
        final FilesystemMetadataProvider filesystemMetadataProvider = new FilesystemMetadataProvider(
                new File(identityProviderMetadataPath));
        filesystemMetadataProvider.setParserPool(parserPool());
        final ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(filesystemMetadataProvider,
                extendedMetadata());
        extendedMetadataDelegate.setMetadataTrustCheck(true);
        extendedMetadataDelegate.setMetadataRequireSignature(false);
        backgroundTaskTimer.purge();
        return extendedMetadataDelegate;
    }

    /* IDP Discovery Service */
    @Bean
    public SAMLDiscovery samlDiscovery() {
        SAMLDiscovery idpDiscovery = new SAMLDiscovery();
        idpDiscovery.setIdpSelectionPath("/api/auth/discovery");
        return idpDiscovery;
    }

    /* Setup advanced info about metadata */
    @Bean
    public ExtendedMetadata extendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setLocal(true);
        extendedMetadata.setIdpDiscoveryEnabled(true);
        extendedMetadata.setIdpDiscoveryURL(identityProviderDiscoveryUrl);
        extendedMetadata.setIdpDiscoveryResponseURL(identityProviderDiscoveryResponseUrl);
        extendedMetadata.setSignMetadata(true);
        extendedMetadata.setEcpEnabled(true);
        return extendedMetadata;
    }

    /* Entry point to initialize authentication, default values taken from properties file */
    @Bean
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }

    @Bean
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        return webSSOProfileOptions;
    }

    @Bean
    public KeyManager keyManager() {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource storeFile = loader.getResource(samlKeystoreLocation);
        Map<String, String> passwords = new HashMap<>();
        passwords.put(samlKeystoreAlias, samlKeystorePassword);
        return new JKSKeyManager(storeFile, samlKeystorePassword, passwords, samlKeystoreAlias);
    }

    @Bean
    public SingleLogoutProfile logoutProfile() {
        return new SingleLogoutProfileImpl();
    }

    @Bean
    public WebSSOProfileECPImpl ecpProfile() {
        return new WebSSOProfileECPImpl();
    }

    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl();
    }

    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        return new WebSSOProfileConsumerImpl();
    }

    @Bean
    public SAMLDefaultLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    @Bean
    public static SAMLBootstrap samlbootstrap() {
        return new SAMLBootstrap();
    }

    @Bean
    public SAMLContextProviderImpl contextProvider() {
        return new SAMLContextProviderImpl();
    }

    /* SAML Authentication Provider responsible for validating of received SAML messages */
    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
        samlAuthenticationProvider.setUserDetails(userService);
        samlAuthenticationProvider.setForcePrincipalAsString(false);
        return samlAuthenticationProvider;
    }

    @Bean
    public HttpClient httpClient() {
        return new HttpClient(this.multiThreadedHttpConnectionManager);
    }

    @Bean(name = "parserPoolHolder")
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }

    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
        return new StaticBasicParserPool();
    }

    @Bean
    public VelocityEngine velocityEngine() {
        return VelocityFactory.getEngine();
    }

    public void shutdown() {
        this.backgroundTaskTimer.purge();
        this.backgroundTaskTimer.cancel();
        this.multiThreadedHttpConnectionManager.shutdown();
    }

    public void init() {
        this.backgroundTaskTimer = new Timer(true);
        this.multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
    }
}
