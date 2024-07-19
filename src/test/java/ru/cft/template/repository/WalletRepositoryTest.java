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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.cft.template.core.entity.Wallet;
import ru.cft.template.core.repository.WalletRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WalletRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    public void setUp() {
        Wallet wallet = new Wallet();
        wallet.setWalletNumber(123456789L);
        wallet.setWalletBalance(10);
        wallet.setLastUpdate(LocalDateTime.now());
        entityManager.persistAndFlush(wallet);
    }

    @AfterEach
    public void tearDown() {
        entityManager.clear();
    }

    @Test
    public void testFindByWalletNumber() {
        Optional<Wallet> found = walletRepository.findByWalletNumber(123456789L);
        assertTrue(found.isPresent());
        assertEquals(123456789L, found.get().getWalletNumber());
    }
}
