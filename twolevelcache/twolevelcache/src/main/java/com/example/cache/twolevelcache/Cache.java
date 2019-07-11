package com.example.cache.twolevelcache;


public class Cache {

    //Serves as an abstraction for level1 and level2 cache. Performs get/put on level1 and depending on results does the ame on level2cache as well.
    //Integrate level2cache.
    private LevelOneCache l1cache;
    private LevelTwoCache l2cache;

    public Cache(int level1capacity) {
        l2cache = new LevelTwoCache(level1capacity);
//        l1cache = new LevelOneCache(level1capacity);
        l1cache = l2cache.getL1cache();

    }

    public Object get(String key) {
        Object result = l1cache.get(key);
        if (result != null) {
            return result;
        } else {
            //Not present in level1cache so call level2cache and check if present.
            return l2cache.get(key);
        }
    }

    //Need to implement put method and implement Service interface in all 3 cache classes.
    public void put(String key, Object value) {
        l1cache.put(key, value);
    }

}
