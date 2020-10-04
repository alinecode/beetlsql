package org.beetl.sql.core.concat;



import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.DefaultKeyWordHandler;
import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;


import java.util.ArrayList;
import java.util.List;

/**
 * 简单用于构造模板sql，内部{@link AbstractDBStyle} 使用，跟{@link Query} 不同
 * @author xiandafu
 */
public class ConcatContext {

    protected NameConversion nc;
    protected List<Object> vars;
    protected KeyWordHandler keyWordHandler = new DefaultKeyWordHandler();

    SQLTemplateEngine templateEngine = null;

    private ConcatContext(){

    }

    private static ConcatContext create(){
        return new ConcatContext();
    }

    public  Select select(){
        Select select = new Select(this);
        return select;
    }

    public  Delete delete(){
        Delete delete = new Delete(this);
        return delete;
    }

    public  Update update(){
        Update update = new Update(this);
        return update;
    }

    public  Insert insert(){
        Insert insert = new Insert(this);
        return insert;
    }

    public  WhereNode where(){
        WhereNode where = new WhereNode(this);
        return where;
    }

    public static ConcatContext create(NameConversion nc){
        ConcatContext ctx = new ConcatContext();
        ctx.nc = nc;
        return ctx;
    }



    public  static ConcatContext createTemplateContext(NameConversion nc, KeyWordHandler keyWordHandler, SQLTemplateEngine templateEngine){
        ConcatContext ctx = new ConcatContext();
        ctx.nc = nc;
        ctx.templateEngine = templateEngine;

        return ctx;
    }





    protected ConcatBuilder concatBuilder = new ConcatBuilder(this);

    protected void addVar(Object var){
        if(vars==null){
            vars = new ArrayList<>();
        }
        vars.add(var);
    }

    public void setKeyWordHandler(KeyWordHandler keyWordHandler) {
        this.keyWordHandler = keyWordHandler;
    }
}
