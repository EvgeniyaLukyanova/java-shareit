package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository repository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(repository);
    }

    @Test
    void createUser() {
        User user = new User(1L, "user@user.com", "user");
        when(repository.save(user)).thenReturn(user);

        UserDto userDto = UserMapper.toUserDto(user);
        UserDto resultUserDto = userService.createUser(userDto);

        assertEquals(userDto, resultUserDto);
        verify(repository).save(user);
    }

    @Test
    void createUserEmpty() {
        User user = null;
        UserDto userDto = UserMapper.toUserDto(user);
        UserDto resultUserDto = userService.createUser(userDto);

        assertNull(resultUserDto);
        verify(repository, never()).save(user);
    }

    @Test
    void partialUpdate() {
        Long userId = 1L;
        User user = new User(userId, "user@user.com", "user");
        UserDto updateUserDto = new UserDto();
        updateUserDto.setId(userId);
        updateUserDto.setName("update");
        updateUserDto.setEmail("updateName@user.com");

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        UserDto resultUserDto = userService.partialUpdate(updateUserDto, userId);

        assertEquals(userId, resultUserDto.getId());
        assertEquals("updateName@user.com", resultUserDto.getEmail());
        assertEquals("update", resultUserDto.getName());
    }

    @Test
    void deleteUser() {
        Long userId = 1L;
        userService.delete(userId);
        verify(repository, times(1)).deleteById(userId);
    }

    @Test
    void getUsers() {
        Long userId = 1L;
        User user = new User(userId, "user@user.com", "user");
        List<User> userList = List.of(user);
        List<UserDto> userDtoList = List.of(UserMapper.toUserDto(user));

        when(repository.findAll()).thenReturn(userList);

        List<UserDto> resultUserDtoList = userService.getUsers();

        assertEquals(1, resultUserDtoList.size());
        assertEquals(userDtoList.get(0), resultUserDtoList.get(0));
    }

    @Test
    void getUserByIdWhenUserFound() {
        Long userId = 1L;
        User user = new User(userId, "user@user.com", "user");
        UserDto userDto = UserMapper.toUserDto(user);
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        UserDto resultUserDto = userService.getUserById(userId);

        assertEquals(userDto, resultUserDto);
    }

    @Test
    void getUserByIdWhenUserNotFound() {
        Long userId = 1L;
        User user = new User(userId, "user@user.com", "user");
        UserDto userDto = UserMapper.toUserDto(user);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }
}