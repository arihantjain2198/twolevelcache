package com.example.cache.twolevelcache;

import java.util.LinkedHashMap;


class LevelOneCache {

    private int capacity;
    private LinkedHashMap<String, Object> hm;

    public LevelOneCache(int capacity, LevelTwoCache l2cache) {
        this.capacity = capacity;

        this.hm = new LinkedHashMap<String, Object>(capacity, 0.75f, true) {
            private static final long serialVersionUID = 10000L;

            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<String, Object> eldest) {
                boolean removeEldestEntry = size() > LevelOneCache.this.capacity;
                if (removeEldestEntry) {
                    String eldestKey = eldest.getKey();
                    Object eldestValue = eldest.getValue();

//                    System.out.println("eldestKey: " + eldestKey);

                    l2cache.put(eldestKey, eldestValue);
                }
                return removeEldestEntry;
            }

        };
    }

    public Object get(String key) {
        return hm.getOrDefault(key, null);
    }

    public void put(String key, Object value) {
        hm.put(key, value);
    }
}
