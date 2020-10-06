package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.BeanFetch;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany;
import org.beetl.sql.fetch.annotation.FetchOne;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * fetch 入口类，在beetlsql查询返回结果前，最后一步就是确实实体是否有@Fetch注解，如果有，按照fetch注解 抓取额外的对象
 * @author xiandafu
 * @see Fetch
 */
public class DefaultBeanFetch implements BeanFetch {

    static ThreadLocal<FetchContext> local = new ThreadLocal<>();
    ConcurrentHashMap<Class,List<FetchAction>> fetchConfig = new ConcurrentHashMap<>();

    @Override
    public void fetchMore(ExecuteContext ctx , List beans, Annotation annotation) {
        Class target = ctx.target;
        boolean isInit = false;
        try{
            FetchContext fetchContext = local.get();
            if(fetchContext==null){
                isInit = true;
                fetchContext = new FetchContext();
                int level= ((Fetch)annotation).level();
                fetchContext.setLevel( level);
                local.set(fetchContext);
            }
            if(fetchContext.getLevel()==0){
                return ;
            }

            List<FetchAction> listAction = parse(ctx.sqlManager,target);
            for(FetchAction action:listAction){
                int keepLevel = fetchContext.level;
                fetchContext.setLevel(keepLevel-1);
                action.execute(ctx,beans);
                //恢复层级设置
                fetchContext.setLevel(keepLevel);
            }
        }catch(Exception ex){
            throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,ex);
        }
        finally {
            if(isInit){
                local.remove();
            }
        }



    }
    protected List<FetchAction> parse(SQLManager sqlManager,Class target){
        if(fetchConfig.containsKey(target)){
            return fetchConfig.get(target);
        }
        List<FetchAction> actions = new ArrayList<>();
        try {
            PropertyDescriptor[] allPs =  BeanKit.propertyDescriptors(target);
            for(PropertyDescriptor pd:allPs ){
                FetchOne fetchOne = BeanKit.getAnnotation(target,pd.getName(), FetchOne.class);
                if(fetchOne!=null){
                    String fromAttr = fetchOne.value();
                    PropertyDescriptor fromProperty = BeanKit.getPropertyDescriptor(target,fromAttr);
                    Class owner = target;
                    Class fetchTargetType = pd.getPropertyType();
                    PropertyDescriptor toProperty =pd;

                    FetchOneAction action = new FetchOneAction(owner,fetchTargetType,fromProperty,toProperty);
                    actions.add(action);
                }
                FetchMany fetchMany = BeanKit.getAnnotation(target,pd.getName(), FetchMany.class);
                if(fetchMany!=null){
                    PropertyDescriptor beanIdProperty = findIdProperty(target,sqlManager);
                    PropertyDescriptor toProperty = pd;

                    String typeAttr = fetchMany.value();
                    Class classType = pd.getPropertyType();
                    Type type = pd.getReadMethod().getGenericReturnType();
                    if(!List.class.isAssignableFrom(classType)){
                        throw new IllegalStateException("one2Many 类型应该是List");
                    }
                    Class targetType = this.getCollectionType(type);
                    PropertyDescriptor otherTypeFrom = BeanKit.getPropertyDescriptor(targetType,typeAttr);
                    FetchManyAction action = new FetchManyAction(target,targetType,beanIdProperty,otherTypeFrom,toProperty);
                    actions.add(action);
                }

            }
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
        fetchConfig.put(target,actions);
        return actions;
    }


    protected  PropertyDescriptor findIdProperty(Class target,SQLManager sqlManager) throws IntrospectionException{
        List<String> ids  = sqlManager.getClassDesc(target).getIdAttrs();
        if(ids.size()>1){
            //
            throw new UnsupportedOperationException("目前不支持多主键fetch");
        }
        return BeanKit.getPropertyDescriptor(target,ids.get(0));

    }


    /*TODO,与ReturnTypeParser 代码重复*/
    public Class getCollectionType(Type type){
        if(!(type instanceof ParameterizedType) ){
            throw new IllegalStateException("无泛型类型，无法Fetch");
        }
        Class paraType =  getParamterTypeClass(type);
        if(paraType==null){
            throw new IllegalStateException("无泛型类型，无法Fetch");
        }
        return paraType;
    }


    protected Class getParamterTypeClass(Type t) {
        if (t instanceof WildcardType || t instanceof TypeVariable) {
            // 丢失类型
            return null;
        } else if (t instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
        } else {
            throw new UnsupportedOperationException();
        }

    }



}
