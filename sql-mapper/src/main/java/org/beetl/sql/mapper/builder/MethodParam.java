package org.beetl.sql.mapper.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录了mapper方法的一个参数
 * <pre>
 *     public void updateUser(String id,int status);
 * </pre>
 * 分别是[{name:"id",index:0},{name:"status",index:"1"}]
 */
public class MethodParam {
    String name;
    int index;
    /**
     * 在多个参数中，通过@Root注解注明的参数，则在beetlsql执行，能直接用此参数的属性，而不需要加上参数名前缀
     * 如果只有一个参数，则总是"Root"参数
     */
    boolean isRoot;

    public MethodParam(String name,int index,boolean isRoot){
        this.name = name;
        this.index = index;
        this.isRoot = isRoot;
    }

    public MethodParam(){

    }

    public String getParamName(){
        return name;

    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }
}
