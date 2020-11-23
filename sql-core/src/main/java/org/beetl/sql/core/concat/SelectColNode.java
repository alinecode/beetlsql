package org.beetl.sql.core.concat;

import java.util.ArrayList;
import java.util.List;

public class SelectColNode extends TrimSupport implements Output {
    List<ColName> list ;
    protected  boolean count = false;
    protected  boolean all = false;
    public SelectColNode col(String name){
        check();
        list.add(new ColName(name));
        return this;
    }

    protected  void check(){
        if(list==null){
            list = new ArrayList<>();
        }
    }

    protected  void count(){
        this.count = true;
    }
    protected  void all(){
        this.all = true;
    }

    @Override
    public void toSql(ConcatBuilder sb){
        if(count){
            sb.append("count(1)");
        }else if(all){
            sb.append("*");
        }
        else{
            if(trim){
                sb.appendTrimStart();
            }
            int size = list.size();
            for(int i=0;i<size;i++){
                if(i!=0){
                    sb.comma();
                }
                ColName name = list.get(i);
                name.toSql(sb);
            }

            if(trim){
                sb.appendTrimEnd();
            }
        }

    }

}
