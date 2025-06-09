package epam.util;

import epam.entity.User;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(InstancioExtension.class)
class UsernameGeneratorListenerTest {

    @Test
    void generateUsername_ShouldGenerateUsername_WhenUsernameIsNull() {
        UsernameGeneratorListener usernameGeneratorListener = new UsernameGeneratorListener();

        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername(null);

        usernameGeneratorListener.generateUsername(user);

        assertEquals("john.doe", user.getUsername());
    }
}