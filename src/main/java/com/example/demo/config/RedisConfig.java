package com.example.demo.config;


import com.example.demo.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, User> userRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, User> template = new RedisTemplate<>();

        StringRedisSerializer keySerializer =
                new StringRedisSerializer();

        Jackson2JsonRedisSerializer<User> valueSerializer =
                new Jackson2JsonRedisSerializer<>(User.class);

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();

        return template;
    }
}