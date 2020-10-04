package org.beetl.sql.fetech;

import org.beetl.sql.clazz.ClassAnnotation;
import org.beetl.sql.clazz.ClassDesc;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类的某些属性可以通过属性再次查询获取
 * <pre>{@code
 *     public class User{
 *     	  private Long id;
 *     	  private Long departmentId
 *        @FetchOne("deparmtId")
 *        Department department;
 *     }
 *
 * }</pre>
 * @author xiandafu
 *
 */
public class FetchOneAction extends   AbstractFetchAction {
    Class owner;
    Class target;
    PropertyDescriptor from;
    PropertyDescriptor to;

    /**
     * 以User例子来说
     * @param owner owner是user对象
     * @param target department对象
     * @param from user#departmentId
     * @param to user#department
     */
    public FetchOneAction(Class owner, Class target, PropertyDescriptor from, PropertyDescriptor to){
        this.owner = owner;
        this.target = target;
        if(from==null){
        	throw new IllegalArgumentException("FetchOne 未正确指定属性 "+ owner+" to "+ target);
		}
        this.from = from;
        this.to = to;
    }

    /**
     *
     * @param ctx
     * @param list  以User例子，list是包含User对象
     */
    public void execute(ExecuteContext ctx,List list){
        try{
            Method fromReadMethod = from.getReadMethod();
            Method toWriteMethod = to.getWriteMethod();
            Map<Object,List<Object>> todoLoad = new HashMap<>();
            for(int i=0;i<list.size();i++){
                Object obj = list.get(i);
                Object cached  = queryFromCache(ctx.sqlManager,obj);
                //检测缓存
                if(cached!=null&&obj!=cached){
                    list.remove(i);
                    //使用缓存对象代替，不需要操作数据库，也避免循环引用
                    list.add(i,cached);
                    return ;
                }
                //缓存自己，也避免未来循环引用
                addCached(ctx.sqlManager,obj);



                Object otherTypeId = fromReadMethod.invoke(obj,new Object[0]);
                if(otherTypeId==null){
                    continue;
                }
                Object toObject = queryFromCache(target,otherTypeId);
                if(toObject==null){
                    //先缓存需要从数据库加载的对象的key，deptId->user
                    List<Object> objs = todoLoad.get(otherTypeId);
                    if(objs==null){
                        objs = new ArrayList<>();
                        todoLoad.put(otherTypeId,objs);
                    }
                    objs.add(obj);
                    continue;
                }

                //成功赋值
                toWriteMethod.invoke(obj,toObject);

            }

            if(todoLoad.isEmpty()){
                return ;
            }

            //合并查询
            List<Object> keys = new ArrayList<>(todoLoad.keySet());
            List<Object> dbsObject = ctx.sqlManager.selectByIds(target,keys);
            // deptId--> department
            Map<Object,Object> dbsMap = toMap(ctx.sqlManager,dbsObject,target);
            for(int i=0;i<keys.size();i++){
                //deptId
                Object key = keys.get(i);
                //User
                List objs = todoLoad.get(key);
                //department
                Object toObject = dbsMap.get(key);
                if(toObject==null){
                    //无值，忽略,未来可考虑配置是否可以忽略或者抛错
                    continue;
                }
                for(Object obj:objs){
                    toWriteMethod.invoke(obj,toObject);
                }
                addCached(toObject,key);
            }
        }catch(InvocationTargetException ex){
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,ex.getTargetException());
        }catch(Exception ex){
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,ex);
        }

    }

    public Map<Object,Object> toMap(SQLManager sqlManager, List<Object> dbsObject, Class queryTarget){
        Map<Object,Object>  map = new HashMap<>();
        String idAttr = sqlManager.getClassDesc(target).getIdAttr();
        dbsObject.forEach(obj->{
            Object key = BeanKit.getBeanProperty(obj,idAttr);
            map.put(key,obj);
        });

        return map;

    }

}
