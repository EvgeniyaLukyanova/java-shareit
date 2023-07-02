package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import javax.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserRepository userRepository;
    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(userRepository);
    }

    @Test
    void partialUpdate() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        em.persist(user);
        em.flush();

        UserDto userDto = new UserDto();
        userDto.setName("updateUser");

        UserDto userDtoResponse =  service.partialUpdate(userDto, user.getId());

        assertEquals(userDtoResponse.getName(), userDto.getName());
        assertEquals(userDtoResponse.getEmail(), user.getEmail());
    }

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
    }
}