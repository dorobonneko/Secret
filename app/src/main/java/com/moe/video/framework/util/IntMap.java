package com.moe.video.framework.util;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.Set;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.HashMap;
import java.util.ArrayList;

public class IntMap <V extends Object>{
    private ArrayList<Integer> keys=new ArrayList<>();
    private HashMap<Integer,V> values=new HashMap<>();
    
    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
    public int indexOfKey(int key){
        return keys.indexOf(key);
    }
    public int getKey(int index){
        return keys.get(index);
    }
    public boolean containsKey(int p1) {
        return values.containsKey(p1);
    }

   
    public boolean containsValue(V p1) {
        return values.containsValue(p1);
    }

    public V get(int p1) {
        return values.get(p1);
    }

    public V put(int p1, V p2) {
        keys.add(p1);
        return values.put(p1,p2);
    }

    
    
    
}
