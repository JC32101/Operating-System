package framework;
import utils.SimpleHashMap;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ConcurrentKVStore {

    SimpleHashMap[] maps;
    //each arraylist represents the keylist of a given partition/map
    ArrayList<String>[] keys;

    public void setup(int numMaps) {
        keys = new ArrayList[numMaps];
        maps = new SimpleHashMap[numMaps];
        for (int i = 0; i < numMaps; i++) {
            maps[i] = new SimpleHashMap();
        }
    }

    public void put(KVPair key, int mapNum) {
        if (!keys[mapNum].contains(key.getKey())) {
            keys[mapNum].add(key.getKey());
        }
        maps[mapNum].put(key.getKey(), 1);
    }

    public ArrayList<String> getKeys(int mapNum) {
        return keys[mapNum];
    }
}

