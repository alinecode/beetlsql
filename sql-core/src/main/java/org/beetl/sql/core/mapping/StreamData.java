package org.beetl.sql.core.mapping;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 把查询结果集转化为StreamData，提供了遍历方法用于逐一处理结果集
 * 注意：用户必须在事物内调用此类的遍历方法，如果不在事物上下文里调用，会抛出"Stream查询只允许在事物环境里执行"错误。
 * @param <T>
 * @author xiandafu
 */
public class 	StreamData<T> {
    ResultSet rs;
    ExecuteContext ctx;
    Class<T> clazz;
    BeanProcessor beanProcessor = null;
    protected  boolean done = false;
    protected  Type mappingType = null;
    enum Type{
        POJO,BASE,MAP;
    }
    //可能自定义的RowMapper
    RowMapper rowMapper;
    Annotation config;

    public StreamData(ResultSet rs, ExecuteContext ctx,Class<T> clazz){
        this.rs = rs;
        this.ctx = ctx;
        this.clazz = clazz;
        config4stream(rs);
        if (Map.class.isAssignableFrom(clazz)){
            mappingType = Type.MAP;
        }else if(BeanKit.isBaseDataType(clazz)){
            mappingType = Type.BASE;
        }else{
            mappingType = Type.POJO;
        }
    }

    public void setRowMapper(RowMapper rowMapper, Annotation config){
        this.rowMapper = rowMapper;
        this.config = config;
    }

    public void foreach(Consumer<T> consumer){
        if(done){
            throw new IllegalStateException("遍历已经结束");
        }
        T t  = null;
        done = true;
        while((t=next())!=null){
            consumer.accept(t);
        }
    }

    protected  void config4stream(ResultSet rs ){
        ctx.sqlManager.getDbStyle().streamConfig(rs);
    }

    public T next(){
        try{
            SQLManager sqlManager = ctx.sqlManager;
            if(!sqlManager.getDs().isTransaction()){
                //让事物管理器自动释放数据库链接。否则，可能链接泄露
                throw new UnsupportedOperationException("Stream查询只允许在事物环境里执行");
            }
            if(beanProcessor==null){
                beanProcessor = ctx.sqlManager.getDefaultBeanProcessors();
            }
            while(rs.next()){
                T t  = null;
                if(mappingType==Type.POJO){
                    t =  beanProcessor.toBean(ctx,rs,clazz);
                }else if(mappingType == Type.BASE){
                    t = (T)beanProcessor.toBaseType(ctx,clazz,rs);
                }else{
                    t = (T)beanProcessor.toMap(ctx,clazz,rs);
                }

                return t;
            }
            return  null;
        }catch(SQLException exe){
            throw new BeetlSQLException(BeetlSQLException.ERROR,exe.getMessage(),exe);
        }

    }




}
