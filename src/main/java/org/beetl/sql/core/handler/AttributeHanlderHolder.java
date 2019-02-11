package org.beetl.sql.core.handler;

import org.beetl.sql.core.annotatoin.Handler;

import java.lang.annotation.Annotation;

public class AttributeHanlderHolder {
    Annotation sqlAnnotation;
    SQLHandler sqlHanlder;


    public Annotation getSqlAnnotation() {
        return sqlAnnotation;
    }

    public void setSqlAnnotation(Annotation sqlAnnotation) {
        this.sqlAnnotation = sqlAnnotation;
    }

    public SQLHandler getSqlHanlder() {
        return sqlHanlder;
    }

    public void setSqlHanlder(SQLHandler sqlHanlder) {
        this.sqlHanlder = sqlHanlder;
    }

    public boolean containSqlHandler(int type){
//        Handler handlerDefine = sqlAnnotation.getClass().an.getAnnotation(Handler.class);
        Handler handlerDefine  =sqlAnnotation.annotationType().getAnnotation(Handler.class);
        int[] accept = handlerDefine.accept();
        for(int t:accept){
            if(t==type){
                return true;
            }
        }
        return false;
    }
}
