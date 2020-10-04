package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.SqlId;

public abstract class AbstractClassPathSQLLoader extends  AbstractSQLLoader{

   protected  ClassLoaderKit classLoaderKit;

    public ClassLoaderKit getClassLoaderKit() {
        return classLoaderKit;
    }

    public void setClassLoaderKit(ClassLoaderKit classLoaderKit) {
        this.classLoaderKit = classLoaderKit;
    }

    @Override
    public BeetlSQLException getExeception(SqlId sqlId) {
        String path = getPathBySqlId(sqlId);

        String envInfo = path + ".md(sql)" + " sqlLoader:" + this;
        if (exist(sqlId)) {
            envInfo = envInfo + ",文件找到，但没有对应的sqlId";
        } else {
            envInfo = envInfo + ",未找到对应的sql文件";
        }
        return new  BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "未能找到" + sqlId + "对应的sql,搜索路径:" + envInfo);
    }


}
