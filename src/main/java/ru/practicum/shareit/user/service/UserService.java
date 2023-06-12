package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
public class UserService {
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDto createUser(UserDto user) {
        return toUserDto(repository.save(toUser(user)));
    }

    public UserDto partialUpdate(UserDto userDto, Long id) {
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            updateUser(userDto, user);
            return toUserDto(repository.save(user));
        } else {
            return null;
        }
    }

    public UserDto getUserById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ид %s не найден", id)));
        return toUserDto(user);
    }

    public List<UserDto> getUsers() {
        return repository.findAll().stream().map(e -> toUserDto(e)).collect(Collectors.toList());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
