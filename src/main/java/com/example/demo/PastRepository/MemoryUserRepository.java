package com.example.demo.PastRepository;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public MemoryUserRepository() {
        users.put(1L, new User(1L, "Alice", 20));
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        users.put(user.getId(),user);
        return user;
    }

    @Override 
    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }
}
