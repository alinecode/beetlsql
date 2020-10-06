package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * 该类的某些属性可以通过再次查询获取
 * <pre>{@code
 *     public class User{
 *     	  private Long id;
 *     	  private Long departmentId
 *        @FetchOne("deparmtId")
 *        Department department;
 *		  @FetchMany("userId")
 *		  List<AuditLog> logs;
 *     }
 *
 *
 * }</pre>
 * @author xiandafu
 *
 */
public class FetchManyAction extends   AbstractFetchAction {
    Class owner;
    Class target;
    PropertyDescriptor  idProperty;
    PropertyDescriptor otherTypeFrom;
    PropertyDescriptor to;
    public FetchManyAction(Class owner, Class target, PropertyDescriptor idProperty, PropertyDescriptor otherTypeFrom, PropertyDescriptor to){
        this.owner = owner;
        this.target = target;
        this.otherTypeFrom = otherTypeFrom;
        if(otherTypeFrom==null){
        	throw new IllegalArgumentException("未正确指定FetchMany的属性 "+owner+" to "+target);
		}
        this.to = to;
        this.idProperty = idProperty;
    }
    public void execute(ExecuteContext ctx,List list){
        try{
            Method idReadMethod = idProperty.getReadMethod();
            Method fromWriteMethod = otherTypeFrom.getWriteMethod();
            Method toWriteMethod = to.getWriteMethod();
            for(int i=0;i<list.size();i++){
                Object obj = list.get(i);
                Object id = idReadMethod.invoke(obj,new Object[0]);
                Object cached  = queryFromCache(owner,id);
                // 检测缓存
                if(cached!=null&&obj!=cached){
                    list.remove(i);
                    list.add(i,cached);
                    continue;
                }else{
                    addCached(ctx.sqlManager,obj);
                }
                Object template = BeanKit.newInstance(target);
                fromWriteMethod.invoke(template,id);
                List values = ctx.sqlManager.template(template);
                for(int j=0;j<values.size();j++){
                    Object otherObj = values.get(j);
                    Object otherCached = queryFromCache(ctx.sqlManager,otherObj);

                    //检测缓存
                    if(otherCached!=null&&otherObj!=otherCached){
                        values.remove(j);
                        values.add(j,otherCached);
                    }else{
                        addCached(ctx.sqlManager,otherObj);
                    }
                }

                toWriteMethod.invoke(obj,values);
            }

        }catch(InvocationTargetException ex){
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,ex.getTargetException());
        }catch(Exception ex){
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,ex);
        }

    }

}
