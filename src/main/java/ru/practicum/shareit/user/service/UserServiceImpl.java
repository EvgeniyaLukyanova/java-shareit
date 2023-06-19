package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto user) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Transactional
    @Override
    public UserDto partialUpdate(UserDto userDto, Long id) {
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserMapper.updateUser(userDto, user);
            return UserMapper.toUserDto(user);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ид %s не найден", id)));
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream().map(e -> UserMapper.toUserDto(e)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
