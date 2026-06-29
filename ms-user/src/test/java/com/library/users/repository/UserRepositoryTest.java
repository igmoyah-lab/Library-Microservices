package com.library.users.repository;

import com.library.users.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAuthEmailIgnoreCase_shouldFindUserIgnoringCase() {
        User user = new User();
        user.setAuthEmail("usuario@test.com");
        user.setFullName("Usuario Test");
        user.setPhone("912345678");
        user.setAddress("Santiago");

        userRepository.saveAndFlush(user);

        var result = userRepository.findByAuthEmailIgnoreCase("USUARIO@TEST.COM");

        assertTrue(result.isPresent());
        assertEquals("usuario@test.com", result.get().getAuthEmail());
        assertEquals("Usuario Test", result.get().getFullName());
    }

    @Test
    void existsByAuthEmailIgnoreCase_shouldReturnTrueWhenEmailExists() {
        User user = new User();
        user.setAuthEmail("existe@test.com");
        user.setFullName("Usuario Existe");
        user.setPhone("987654321");
        user.setAddress("Valparaíso");

        userRepository.saveAndFlush(user);

        boolean exists = userRepository.existsByAuthEmailIgnoreCase("EXISTE@TEST.COM");

        assertTrue(exists);
    }
}