package org.beetl.sql.core.concat;


import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;

/**
 * 拼接sql模板的类，可以拼接sql语句，也借助模板引擎拼接动态sql
 * @author xiandafu
 * @see SQLTemplateEngine
 */
public class  ConcatBuilder  {
    ConcatContext ctx;
    public ConcatBuilder(ConcatContext ctx){
        this.ctx = ctx;
    }
    StringBuilder sb = new StringBuilder();
    public ConcatBuilder appendCol(String colName){
        KeyWordHandler kh = ctx.keyWordHandler;
        String col = kh.getCol(colName);
        sb.append(col).append(" ");
        return this;
    }

    public ConcatBuilder appendTable(String tableName){
        KeyWordHandler kh = ctx.keyWordHandler;
        String table = kh.getTable(tableName);
        sb.append(table).append(" ");
        return this;
    }

    public ConcatBuilder appendTable(Class table){
        String tableName = ctx.nc.getTableName(table);
        return this.appendTable(tableName);
    }


    public ConcatBuilder comma(){
        sb.append(",");
        return this;
    }

    public ConcatBuilder assign(){
        sb.append("=");
        return this;
    }

    public ConcatBuilder valueHolder(){
        sb.append("?");
        return this;
    }

    public ConcatBuilder leftBracket(){
        sb.append("(");
        return this;
    }

    public ConcatBuilder rightBracket(){
        sb.append(")");
        return this;
    }

    public ConcatBuilder cr(){
        sb.append("\n");
        return this;
    }



    public ConcatBuilder appendVar(String var){
        ctx.templateEngine.genVar(this,var);
        return this;
    }
    public ConcatBuilder testVar(String varName,String col){
        ctx.templateEngine.genTestVar(this,varName,col);
        return this;
    }

    public ConcatBuilder testVar(String varName){
        ctx.templateEngine.genTestVar(this,varName);
        return this;
    }




    public ConcatBuilder appendTrimStart(){
        this.cr();
        ctx.templateEngine.genTrimStart(this);
        return this;
    }

    public ConcatBuilder appendTrimEnd(){
        this.cr();
        ctx.templateEngine.genTrimEnd(this);
        return this;
    }

    public ConcatBuilder appendIfNotEmptyStart(String name){
        this.cr();
        ctx.templateEngine.genIfNotEmptyStart(this,name);
        return this;
    }

    public ConcatBuilder appendIfNotEmptyEnd(){
        this.cr();
        ctx.templateEngine.genIfNotEmptyEnd(this);
        return this;
    }






    public ConcatBuilder append(String str){
        sb.append(str).append(" ");
        return this;
    }



    public String toString(){
        return sb.toString();
    }

    public ConcatContext getCtx() {
        return ctx;
    }
}
