package org.beetl.sql.core.db;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *  SQLite 数据库
 * Created by mikey.zhaopeng on 2015/11/18.
 * http://zhaopeng.me
 */
public class SQLiteStyle extends AbstractDBStyle {

    @Override
    public String getPageSQL(String sql) {
        return sql+ this.getOrderBy()+" \nlimit " + HOLDER_START + PAGE_SIZE + HOLDER_END + " offset " + HOLDER_START + OFFSET + HOLDER_END;
    }

    @Override
    public void initPagePara(Map<String, Object> param,long start,long size) {
        param.put(DBStyle.OFFSET,start-(this.offsetStartZero?0:1));
        param.put(DBStyle.PAGE_SIZE,size);
    }

    public SQLiteStyle() {
    }

    

    @Override
    public String getName() {
        return "sqlite";
    }

    @Override
    public String getEscapeForKeyWord(){
        return "`";
    }
}
