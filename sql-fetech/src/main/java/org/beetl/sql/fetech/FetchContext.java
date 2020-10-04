package org.beetl.sql.fetech;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FetchContext {
    //嵌套深度
    int level =1;
    /*保存已经fetch的数据，避免循环加载*/
    Map<Class, Map<Key,Object>> loadedCache = new HashMap<>();
    public void add(Class target,Object key,Object value){
        Map<Key,Object> map = loadedCache.get(target);
        if(map==null){
            map = new HashMap<>();
            loadedCache.put(target,map);
        }
        map.put(new Key(key),value);
    }

    public Object find(Class target,Object key){
        Map<Key,Object> map = loadedCache.get(target);
        if(map==null){
            return null;
        }

        return map.get(new Key(key));
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

 class Key{
    Object obj;
    public Key(Object obj){
        this.obj = obj;
    }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Key key = (Key) o;
         return Objects.equals(obj, key.obj);
     }

     @Override
     public int hashCode() {
         return Objects.hash(obj);
     }
 }
