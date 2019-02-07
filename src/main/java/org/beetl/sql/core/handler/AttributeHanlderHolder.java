package org.beetl.sql.core.handler;

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
}
