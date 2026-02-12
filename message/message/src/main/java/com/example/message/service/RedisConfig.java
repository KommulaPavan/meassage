package com.example.message.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }



        @Bean
        public LettuceConnectionFactory redisConnectionFactory() {

            RedisStandaloneConfiguration config =
                    new RedisStandaloneConfiguration(
                            "redis-10406.c257.us-east-1-3.ec2.cloud.redislabs.com",
                            10406
                    );

            config.setUsername("default");
            config.setPassword("yWytGTaCxQXj9IG6gSQwPhTnVwJrsDos");
            config.setDatabase(0);

            return new LettuceConnectionFactory(config);
        }

        @Bean
        public StringRedisTemplate stringRedisTemplate(
                RedisConnectionFactory factory) {
            return new StringRedisTemplate(factory);
        }
    }



