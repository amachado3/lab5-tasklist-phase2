package cncs.academy.ess.service;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TodoUserService {
    private final UserRepository repository;

    public TodoUserService(UserRepository userRepository) {
        this.repository = userRepository;
    }
    public User addUser(String username, String password) throws NoSuchAlgorithmException {

        try {
            byte[] salt = generateSalt(); // Generate a random salt
            String hashedPassword = Utils.passwordToHash(password, salt);
            User user = new User(username, hashedPassword);
            int id = repository.save(user);
            user.setId(id);
            return user;
        } catch (Exception e) {
            throw new NoSuchAlgorithmException(e);
        }
    }
    public User getUser(int id) {
        return repository.findById(id);
    }

    public void deleteUser(int id) {
        repository.deleteById(id);
    }

    public String login(String username, String password) throws NoSuchAlgorithmException {
        try {
            User user = repository.findByUsername(username);
            if (user == null) {
                return null;
            }
            if (comparePassword(username, password)) {
                return createAuthToken(username);
            }
            return null;
        } catch (Exception e) {
            throw new NoSuchAlgorithmException(e);
        }
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes for the salt
        random.nextBytes(salt);
        return salt;
    }

    private boolean comparePassword(String username, String password) throws Exception {
        User user = repository.findByUsername(username);
        if (user == null) {
            return false;
        }
        String pass_db = user.getPassword();
        int index = pass_db.indexOf(":");
        String salt_db = pass_db.substring(index + 1);
        byte[] salt = salt_db.getBytes(StandardCharsets.ISO_8859_1);

        String newHashedPassword = Utils.passwordToHash(password, salt);

        return pass_db.equals(newHashedPassword);
    }

    private String createAuthToken(String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer("lab2-api")
                    .withClaim("username",username)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                    .sign(algorithm);
            return "Bearer " + token;
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new JWTCreationException("Error creating JWT", exception);
        }
    }
}
