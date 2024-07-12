package vn.iostar.postservice.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {
    void set(String key, Object value);
    void setTimeToLive(String key, long timeToLive);
    void hashSet(String key, String field, Object value);
    boolean hashExists(String key, String field);
    boolean exists(String key);
    Object get(String key);
    Map<String,Object> getFields(String key);
    Object hashGet(String key, String field);
    List<Object> hashGetByFieldPrefix(String key, String fieldPrefix);
    Set<String> getFieldPrefix(String key);
    void delete(String key);
    void delete(String key, String field);
    void delete(String key, List<String> fields);
}
