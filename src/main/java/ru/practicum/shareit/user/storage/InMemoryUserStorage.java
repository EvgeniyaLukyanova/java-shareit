package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Long uniqueId = Long.valueOf(0);
    public final Map<Long, User> users = new HashMap<>();

    private Long getUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    @Override
    public User createUser(User user) {
        if (user != null) {
            user.setId(getUniqueId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user != null) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
    }
}
