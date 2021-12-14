package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.user.User;
import at.tuwien.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PersistenceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void addUser_hasCreated_succeeds() {
        final User user = User.builder()
                .oId(USER_1_OID)
                .firstname(USER_1_FIRSTNAME)
                .lastname(USER_1_LASTNAME)
                .email(USER_1_EMAIL)
                .build();

        /* test */
        final User out = userRepository.save(user);
        assertEquals(USER_1_OID, out.getOId());
        assertEquals(USER_1_FIRSTNAME, out.getFirstname());
        assertEquals(USER_1_LASTNAME, out.getLastname());
        assertEquals(USER_1_EMAIL, out.getEmail());
        assertNotNull(out.getCreated());
    }

}