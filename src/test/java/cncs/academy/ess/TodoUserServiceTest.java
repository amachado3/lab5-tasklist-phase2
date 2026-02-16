package cncs.academy.ess;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.service.TodoUserService;
import cncs.academy.ess.service.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.auth0.jwt.JWT;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TodoUserServiceTest {

    UserRepository mockRepo = mock(UserRepository.class);

    private TodoUserService service;

    @BeforeEach
    void setUp() {
        service = new TodoUserService(mockRepo);
    }

    @Test
    void login_shouldReturnValidJWTTokenWhenCredentialsMatch() throws Exception {
        String username = "jane";
        String password = "pass";
        String s = "salt";
        byte[] salt = s.getBytes(StandardCharsets.ISO_8859_1);

        String hashedPassword = Utils.passwordToHash(password, salt);

        User user = new User(username, hashedPassword);

        when(mockRepo.findByUsername(username)).thenReturn(user);

        String token = service.login(username, password);

        assertEquals("Bearer", token.substring(0,6));

        String claim = JWT.decode(token.substring(7)).getClaim("username").asString();
        String issuer = JWT.decode(token.substring(7)).getIssuer();
        String algorithm = JWT.decode(token.substring(7)).getAlgorithm();

        assertEquals(username, claim);
        assertEquals("lab2-api", issuer);
        assertEquals("HS256", algorithm);
    }
}
