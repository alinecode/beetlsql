package org.beetl.sql.fetch;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
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
import java.util.Collection;
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

	/**
	 * 获取目标类有多少属性需要Fetch
	 * @param sqlManager
	 * @param owner
	 * @return
	 */
    protected List<FetchAction> parse(SQLManager sqlManager,Class owner){
        if(fetchConfig.containsKey(owner)){
            return fetchConfig.get(owner);
        }
        List<FetchAction> actions = new ArrayList<>();
        try {
            Collection<PropertyDescriptorWrap> allPs =  BeanKit.getClassProperty(owner).values();
            for(PropertyDescriptorWrap pd:allPs ){

				List<Annotation> allAnnotation=BeanKit.getAllAnnotation(owner,pd.getProp().getName());
				for(Annotation annotation:allAnnotation){
					if(annotation instanceof  FetchOne){
						FetchOne fetchOne = (FetchOne)annotation;
						String fromAttr = fetchOne.value();
						PropertyDescriptorWrap fromProperty = BeanKit.getPropertyDescriptorWrap(owner,fromAttr);
						Class fetchTargetType = pd.getProp().getPropertyType();
						PropertyDescriptorWrap toProperty =pd;
						FetchOneAction action = new FetchOneAction(fromProperty);
						action.init(owner,fetchTargetType,fetchOne,pd);
						actions.add(action);
						break;

					}else if(annotation instanceof  FetchMany){
						FetchMany fetchMany = (FetchMany)annotation;
						PropertyDescriptorWrap beanIdProperty = findIdProperty(owner,sqlManager);
						PropertyDescriptorWrap toProperty = pd;

						String typeAttr = fetchMany.value();
						Class classType = pd.getProp().getPropertyType();
						Type type = pd.getProp().getReadMethod().getGenericReturnType();
						if(!List.class.isAssignableFrom(classType)){
							throw new IllegalStateException("one2Many 类型应该是List");
						}
						Class targetType = ParameterTypUtil.getCollectionType(type);
						PropertyDescriptorWrap otherTypeFrom = BeanKit.getPropertyDescriptorWrap(targetType,typeAttr);
						FetchManyAction action = new FetchManyAction(beanIdProperty,otherTypeFrom);
						action.init(owner,targetType,fetchMany,pd);
						actions.add(action);
						break;
					}
					//额外扩展,如果注解包含了Builder，且其类为FetchAction子类
					Builder builder = annotation.annotationType().getAnnotation(Builder.class);
					if(builder!=null){
						Class extFetchCls = builder.value();
						if(FetchAction.class.isAssignableFrom(extFetchCls)){
							FetchAction action = (FetchAction)BeanKit.newInstance(extFetchCls);
							Class classType = pd.getProp().getPropertyType();
							Type type = pd.getProp().getReadMethod().getGenericReturnType();
							Class targetType = classType;
							if(List.class.isAssignableFrom(classType)){
								targetType = BeanKit.getCollectionType(type);
							}
							action.init(owner,targetType,annotation,pd);
							actions.add(action);
						}
						break;
					}
				}

            }
        } catch (IntrospectionException e) {
            throw new BeetlSQLException(BeetlSQLException.ERROR,e);
        }
        fetchConfig.put(owner,actions);
        return actions;
    }




    protected  PropertyDescriptorWrap findIdProperty(Class target,SQLManager sqlManager) throws IntrospectionException{
        String id   = sqlManager.getClassDesc(target).getIdAttr();
        return BeanKit.getPropertyDescriptorWrap(target,id);

    }




}
