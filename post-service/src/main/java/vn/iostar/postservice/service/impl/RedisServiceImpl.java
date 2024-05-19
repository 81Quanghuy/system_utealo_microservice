package vn.iostar.postservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.service.RedisService;


import java.util.*;
import java.util.concurrent.TimeUnit;
@Service
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;


    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(String key, long timeToLive) {
        redisTemplate.expire(key, timeToLive, TimeUnit.DAYS);

    }

    @Override
    public void hashSet(String key, String field, Object value) {
        hashOperations.put(key, field, value);
    }

    @Override
    public boolean hashExists(String key, String field) {
        return hashOperations.hasKey(key, field);
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<String, Object> getFields(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public Object hashGet(String key, String field) {
        return hashOperations.get(key, field);
    }

    @Override
    public List<Object> hashGetByFieldPrefix(String key, String fieldPrefix) {
        List<Object> objects = new ArrayList<>();
        Map<String,Object> hashEntries = hashOperations.entries(key);
        for (Map.Entry<String, Object> entry : hashEntries.entrySet()) {
            if (entry.getKey().startsWith(fieldPrefix)) {//bắt đầu bằng fieldPrefix
                objects.add(entry.getValue());
            }
        }
        return objects;
    }

    @Override
    public Set<String> getFieldPrefix(String key) {
        return hashOperations.entries(key).keySet();
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);

    }

    @Override
    public void delete(String key, String field) {
        hashOperations.delete(key, field);

    }

    @Override
    public void delete(String key, List<String> fields) {
        hashOperations.delete(key, fields.toArray());

    }


}