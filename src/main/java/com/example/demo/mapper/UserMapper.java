package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.demo.entity.User;

import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(@Param("id") long id);
    List<User> findAll();

    Long countAll();
    List<User> findPage(
        @Param ("offset") long offset,
        @Param ("size") int size);
    long countByName(@Param ("name") String name);
    List<User> findPageByName(
        @Param ("name") String name,
        @Param ("offset") long offset,
        @Param ("size") int size);

    int insert(User user);
    int update(User user);
    int deleteById(long id);
}
