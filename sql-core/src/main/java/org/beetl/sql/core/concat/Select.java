package org.beetl.sql.core.concat;


import org.beetl.sql.annotation.entity.View;
import org.beetl.sql.clazz.kit.BeanKit;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;

public class Select extends WhereNode {

    String mainTable;
    SelectColNode colNode;

    boolean isAll = false;
    public Select(ConcatContext ctx) {
        super(ctx);
    }



    public Select col(String name) {
        checkCol();
        colNode.col(name);
        return this;
    }

    public Select cols(String... names) {
        checkCol();
        for(String name:names){
            colNode.col(name);
        }

        return this;
    }
    public Select all(){
        checkCol();
        colNode.all();
        return this;
    }

    public Select all(Class pojoClass,Class viewType){
        checkCol();
        try {
            PropertyDescriptor[] propertyDescriptors = BeanKit.propertyDescriptors(pojoClass);
            HashSet<String> cols = new HashSet<>();
            for(PropertyDescriptor ps:propertyDescriptors){
                View view = BeanKit.getAnnotation(pojoClass,ps.getName(), View.class);
                if(view!=null&&BeanKit.containViewType(view.value(),viewType)){
                    colNode.col(ctx.nc.getColName(pojoClass,ps.getName()));
                }
            }
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }



    public Select from(Class target) {
        mainTable = ctx.nc.getTableName(target);
        mainTable = ctx.keyWordHandler.getTable(mainTable);

        return this;
    }

    public Select from(String table) {
        this.mainTable = ctx.keyWordHandler.getTable(table);
        return this;
    }

    public Select count(){
        checkCol();
        colNode.count();
        return this;
    }



    protected void checkCol() {
        if (colNode == null) {
            colNode = new SelectColNode();
        }
    }



    @Override
    public void toSql(ConcatBuilder sb) {
        sb.append("select");
        if(isAll){
            sb.append("*");
        }else {
            colNode.toSql(sb);
        }


        sb.append("from").append(mainTable);
        //where builder
        super.toSql(sb);
    }

    public String toSql() {
        ConcatBuilder sb = ctx.concatBuilder;
        this.toSql(sb);
        return sb.toString();
    }


}
