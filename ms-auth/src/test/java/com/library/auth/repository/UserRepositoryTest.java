package com.library.auth.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.library.auth.domain.User;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmailAndActiveTrue_whenUserIsActive_shouldReturnUser() {
        User user = new User("activo@correo.com", "password-encriptada");
        user.setActive(true);
        userRepository.save(user);

        assertTrue(userRepository.findByEmailAndActiveTrue("activo@correo.com").isPresent());
    }

    @Test
    void findByEmailAndActiveTrue_whenUserIsInactive_shouldReturnEmpty() {
        User user = new User("inactivo@correo.com", "password-encriptada");
        user.setActive(false);
        userRepository.save(user);

        assertFalse(userRepository.findByEmailAndActiveTrue("inactivo@correo.com").isPresent());
    }
}
