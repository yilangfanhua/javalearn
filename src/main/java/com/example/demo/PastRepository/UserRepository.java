package com.example.demo.PastRepository;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    User save(User user);
    List<User> findAll();
    boolean deleteById(Long id);
}
