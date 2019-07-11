package com.example.cache.twolevelcache;

import com.example.cache.Service;

public class DataAccessProxyService implements Service {

    //Change from l1cache to use cache class so both l1 and l2 caches can be abstracted.
    private Cache cache;

    public DataAccessProxyService() {
        this(10);
    }

    public DataAccessProxyService(int levelOneCapacity) {
        cache = new Cache(levelOneCapacity);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }
}

