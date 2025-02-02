package com.example.cache.twolevelcache;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        DataAccessProxyService cache = new DataAccessProxyService(2);
        cache.put("1", 1);
        cache.put("2", 2);
        System.out.println(cache.get("1"));       // returns 1
        cache.put("3", 3);    // evicts key 2
        System.out.println(cache.get("2"));       // returns -1 (not found)
        cache.put("4", 4);    // evicts key 1
        System.out.println(cache.get("1"));       // returns -1 (not found)
        System.out.println(cache.get("3"));       // returns 3
        System.out.println(cache.get("4"));       // returns 4
    }
}
