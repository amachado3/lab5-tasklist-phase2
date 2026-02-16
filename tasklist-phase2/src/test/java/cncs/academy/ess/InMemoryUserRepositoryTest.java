package cncs.academy.ess;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void saveAndFindById_ShouldReturnSavedUser() {
        User user = new User("jane", "password");
        int id = repository.save(user);
        User savedUser = repository.findById(id);
        assertEquals(user, savedUser);
    }

    @Test
    void findAll_ShouldReturnAListOfUsers() {
        User user1 = new User("jane", "password");
        User user2 = new User("joe", "password");

        List<User> userslist = new ArrayList<>();
        userslist.add(user1);
        userslist.add(user2);

        repository.save(user1);
        repository.save(user2);
        List<User> users = repository.findAll();

        assertEquals(userslist, users);
    }

    @Test
    void deleteById_ShouldReturnNullWhenFindById() {
        User user = new User("jane", "password");
        int id = repository.save(user);
        assertNotNull(repository.findById(id));
        repository.deleteById(id);
        assertNull(repository.findById(id));
    }

    @Test
    void findByUsername_ShouldReturnSavedUser() {
        User user1 = new User("jane", "password");
        User user2 = new User("joe", "password");
        repository.save(user1);
        repository.save(user2);

        User user = repository.findByUsername("jane");

        assertEquals(user1, user);
    }
}
