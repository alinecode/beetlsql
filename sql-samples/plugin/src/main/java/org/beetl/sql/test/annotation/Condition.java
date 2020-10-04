package org.beetl.sql.test.annotation;

import java.util.Map;
import java.util.TreeMap;

public class Condition {
    public enum Opt {
        equals("="),large(">"),less("<");
        String token ;
        Opt(String token){
            this.token = token;
        }

        public String getToken(){
            return this.token;
        }

    }
    private TreeMap<String,Opt> map = new TreeMap<>();
    public Condition append(String name,Opt opt){
        map.put(name,opt);
        return this;
    }
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Opt> entry : map.entrySet()){
            sb.append(entry.getKey()).append(entry.getValue().getToken()).append("? ").append(" and ");
         }
        sb.setLength(sb.length()-" and ".length());
        return sb.toString();
    }

}
