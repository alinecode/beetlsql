package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.query.Query;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
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

    PropertyDescriptor  idProperty;
    PropertyDescriptor otherTypeFrom;
    public FetchManyAction(PropertyDescriptor idProperty, PropertyDescriptor otherTypeFrom){
        this.otherTypeFrom = otherTypeFrom;
        this.idProperty = idProperty;
    }

	@Override
	public void init(Class owner, Class target, Annotation config, PropertyDescriptor originProperty){
		super.init(owner, target, config, originProperty);
		if(otherTypeFrom==null){
			throw new IllegalArgumentException("未正确指定FetchMany的属性 "+owner+" to "+target);
		}

	}
    public void execute(ExecuteContext ctx,List list){
        try{
            Method idReadMethod = idProperty.getReadMethod();
            Method fromWriteMethod = otherTypeFrom.getWriteMethod();
            Method toWriteMethod = this.originProperty.getWriteMethod();
            for(int i=0;i<list.size();i++){
                Object obj = list.get(i);
                Object id = idReadMethod.invoke(obj,new Object[0]);
                Object cached  = queryFromCache(owner,id);
                // 检测缓存
                if(cached!=null){
                    list.remove(i);
                    list.add(i,cached);
                    if(this.containAttribute(cached,originProperty.getName())){
						//对象的字段已经被fetch过了
						continue;
					}
					obj = cached;
                }else{
					addCached(obj,id);
				}


//                Object template = BeanKit.newInstance(target);
//				fromWriteMethod.invoke(template,id);
//				List values = ctx.sqlManager.template(template);
				Query query = ctx.sqlManager.query(target);
				String colName = ctx.sqlManager.getNc().getColName(target,otherTypeFrom.getName());
				List values  = query.andEq(colName,id).select();

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
                this.addAttribute(obj,originProperty.getName());
            }

        }catch(InvocationTargetException ex){
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,ex.getTargetException());
        }catch(Exception ex){
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,ex);
        }

    }

}
