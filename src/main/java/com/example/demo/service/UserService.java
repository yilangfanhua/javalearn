package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.common.PageResponse;
import com.example.demo.config.RedisConfig;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class UserService {

    private final String USER_KEY_PREFIX="user:";

    private final UserMapper userMapper;

    private final RedisTemplate<String,User> userRedisTemplate;

    public UserService(UserMapper userMapper,RedisTemplate<String,User> userRedisTemplate) {
        this.userMapper = userMapper;
        this.userRedisTemplate=userRedisTemplate;
    }  

    public Optional<User> getById(Long id) {
        String key=cacheKey(id);
        User cacheUser=userRedisTemplate.opsForValue().get(key);
        if(cacheUser!=null){
            return Optional.of(cacheUser);
            
        }
        User databaseUser=userMapper.findById(id);
        if(databaseUser!=null){
            saveToCache(databaseUser);
        }
        return Optional.ofNullable(databaseUser);
    }

    public List<User> getAll() {
        return userMapper.findAll();
    }

    public PageResponse<User> getPage(int page,int size){
        validatePage(page,size);
        long offset=(long)(page-1)*size;
        long total=userMapper.countAll();

        List<User> users=userMapper.findPage(offset,size);

        return PageResponse.of(users, page, size, total);
        
    }

    private void validatePage(int page, int size) {
        // TODO Auto-generated method stub
        if (page < 1) {
            throw new IllegalArgumentException(
                    "page 必须大于等于 1"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "size 必须在 1 到 100 之间"
            );
        }
    
        //throw new UnsupportedOperationException("Unimplemented method 'ValiddataPage'");
    }

    public PageResponse<User> searchByName(
            String name,
            int page,
            int size) {

        validatePage(page, size);

        String keyword = name == null
                ? ""
                : name.trim();

        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("name 不能为空");
        }

        long offset = (long) (page - 1) * size;
        long total = userMapper.countByName(keyword);

        List<User> users = userMapper.findPageByName(
                keyword,
                offset,
                size
        );

        return PageResponse.of(
                users,
                page,
                size,
                total
        );
    }

    public User create(User user) {
        userMapper.insert(user);
        saveToCache(user);
        return user;
    }

    public Optional<User> update(Long id, User user) {
        User existingUser = userMapper.findById(id);

        if (existingUser == null) {
            return Optional.empty();
        }

        user.setId(id);
        userMapper.update(user);
        saveToCache(user);
        return Optional.of(user);
    }

    public boolean deleteById(Long id) {
        int affecteRows=userMapper.deleteById(id) ;
        if(affecteRows>0){
            userRedisTemplate.delete(cacheKey(id));
            return true;
        }
        return false;
    }


    private String cacheKey(long id ){
        return USER_KEY_PREFIX+id;
    }

    private void saveToCache(User user){
        userRedisTemplate.opsForValue().set(
            cacheKey(user.getId()),
            user,
            Duration.ofMinutes(10)
        );
    }
}