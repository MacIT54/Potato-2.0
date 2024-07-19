package ru.cft.template.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .firstName("John")
                .lastName("Madison")
                .email("jm1@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber(78005553531L)
                .password("Password123")
                .lastUpdateDate(LocalDateTime.now())
                .registrationDate(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(user);
    }

    @AfterEach
    public void tearDown() {
        entityManager.clear();
    }

    @Test
    public void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("jm1@example.com");
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    public void testFindByPhoneNumber() {
        Optional<User> found = userRepository.findByPhoneNumber(78005553531L);
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }
}
