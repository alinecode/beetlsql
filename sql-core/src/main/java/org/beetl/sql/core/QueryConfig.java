package org.beetl.sql.core;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.mapping.ResultSetMapper;
import org.beetl.sql.core.mapping.RowMapper;

public class QueryConfig {
    Class viewClass;
    Class rowMapperClass;
    Class resultSetClass;

    public Class getViewClass() {
        return viewClass;
    }

    public void setViewClass(Class viewClass) {
        this.viewClass = viewClass;
    }

    public RowMapper getRowMapper() {
        if(rowMapperClass==null){
            return null;
        }
        return (RowMapper)BeanKit.newSingleInstance(rowMapperClass);
    }

    public void setRowMapperClass(Class rowMapperClass) {
        this.rowMapperClass = rowMapperClass;
    }

    public ResultSetMapper getResultSetMapper() {

        if(resultSetClass==null){
            return null;
        }
        return (ResultSetMapper) BeanKit.newSingleInstance(resultSetClass);
    }

    public void setResultSetClass(Class resultSetClass) {
        this.resultSetClass = resultSetClass;
    }

    public void clear(){
        viewClass = null;
        rowMapperClass = null;
        resultSetClass = null;
    }
}
