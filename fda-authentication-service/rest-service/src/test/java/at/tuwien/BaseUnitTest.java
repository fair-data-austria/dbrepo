package at.tuwien;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long USER_1_OID = 1L;
    public final static String USER_1_FIRSTNAME = "Foo";
    public final static String USER_1_LASTNAME = "Bar";
    public final static String USER_1_EMAIL = "foo.bar@example.com";

}
