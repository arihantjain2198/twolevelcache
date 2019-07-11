package com.example.cache.twolevelcache;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class LevelTwoCache {

    private HashMap<String, Integer> map;
    private ArrayList<Object> list;
    private final String fileName = "Objects.ser";
    private int fileSize = 1;
    private LevelOneCache l1cache;

    public LevelTwoCache(int fileSize) {
        map = new HashMap<String, Integer>();
        list = new ArrayList<Object>();
        this.fileSize = fileSize;
        l1cache = new LevelOneCache(this.fileSize, this);
    }

    public LevelOneCache getL1cache() {
        return l1cache;
    }

    public Object get(String key) {
        if (map.containsKey(key)) {
            //The object is present in the 2nd level cache. Read the index from the hashmap and then read the arraylist into memory, remove the object
            //from the cache and restructure the other objects and save back to file. Move this object to the level 1 cache head and also return. (Or maybe
            //defer the moving to LevelOneCache class for separation of concerns). If the key is not present in the map then the object is not present
            //in the cache. Defer it to LevelOneCache so that it can save the object to the head.

            //Problem: Not optimal to read and write to file every time a get is called. But, cannot keep the cache in the memory either. Need to find
            //A solution that might be a trade off between the two.
            Integer index = map.get(key);
            Object value = list.get(index);

            try {
                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    list = (ArrayList<Object>) ois.readObject();
                    ois.close();
                    fis.close();

                    //removing the object from the cache and restructuring the other objects
                    Iterator<Map.Entry<String, Integer>> entryIterator = map.entrySet().iterator();
                    while (entryIterator.hasNext()) {
                        Map.Entry<String, Integer> entry = entryIterator.next();
                        Integer indexVal = entry.getValue();
                        if (indexVal > index) {
                            map.put(entry.getKey(), --indexVal);
                        } else if (indexVal.equals(index)) {
                            entryIterator.remove();
                            list.remove(indexVal.intValue());
                        }
                    }

                    //Moved Object into L1 cache
                    l1cache.put(key, value);

                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(list);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException io) {
                io.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            return value;
        }
        return l1cache.get(key);
    }

    /**
     * Save the object to the file. Instead of saving objects directly, we will use an arraylist in which we will append all
     * the objects and save the objects index in the hashmap. Before every put, check if it is present and perform accordingly.
     * <p>
     * Similar problem as 'get'. Do I need to read and write to file everytime? Plus need to add filesize check before saving to file.
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            } /*else {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<Object>) ois.readObject();
                ois.close();
                fis.close();
            }*/
            //Check if object is already present or not. If present then update it to the index 0 of arraylist and push others down.
            if (map.containsKey(key)) {
                Integer index = map.get(key);

                list.remove(index.intValue());
                list.add(0, value);

                map.put(key, -1);
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    Integer indexVal = entry.getValue();
                    map.put(entry.getKey(), ++indexVal);
                }
            } else {
                int listSize = list.size();
                list.add(value);
                map.put(key, listSize);
            }

            //Control the list size here
            if (list.size() >= fileSize) {
                Iterator<Map.Entry<String, Integer>> entryIterator = map.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, Integer> entry = entryIterator.next();
                    Integer indexVal = entry.getValue();
                    if (indexVal >= fileSize) {
                        list.remove(indexVal.intValue());
                        entryIterator.remove();
                    }
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
