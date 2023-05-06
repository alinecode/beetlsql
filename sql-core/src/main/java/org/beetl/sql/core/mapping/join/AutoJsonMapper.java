package org.beetl.sql.core.mapping.join;

import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SqlId;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据java定义自动生成一个json配置，并交给JsonConfigJoinMapper来解析
 * 需要注意的，目前任何JsonConfigMapper实现映射，都需要确保树节点每层都有值，因为JsonConfigMapper会依据这些值作为key
 * 确保不会重建对象
 * @author xiandafu
 */
public class AutoJsonMapper extends JsonConfigMapper {

	public static int MAX_DEPTH = 4;
    @Override
    protected AttrNode parse(ExecuteContext ctx, Class target, ResultSetMetaData rsmd, Annotation config) throws Exception {
		NameConversion nc = ctx.sqlManager.getNc();
		Map columnIndex = this.getColumnIndex(rsmd);
		Map<String,Object> configMap = new CaseInsensitiveHashMap<>();
		String prefix = "";
		int level=0;
		getMappingByJson(prefix,nc,configMap,target,level);
		AttrNode root =  new AttrNode(null);
		root.initNode(target,configMap,columnIndex);
		return root;
    }
	protected void getMappingByJson(String prefix,NameConversion nc,Map<String,Object> configMap,Class target) throws IntrospectionException {
		getMappingByJson(prefix,nc,configMap,target,0);
	}
    protected void getMappingByJson(String prefix,NameConversion nc,Map<String,Object> configMap,Class target,int level) throws IntrospectionException {
        if(level>MAX_DEPTH){
			return ;
		}
		PropertyDescriptor[] pds = BeanKit.propertyDescriptors(target);
        for(PropertyDescriptor pd:pds){
            Class type = pd.getPropertyType();
            if(type==java.lang.Class.class){
                continue;
            }
            String attr = pd.getName();
            String col = nc.getColName(target,attr);
            String configCol = getPrefix(prefix,col);
            if(BeanKit.isBaseDataType(type)){
                //TODO 自定义注解怎么办
                configMap.put(attr,configCol);
                continue;
            }
            Class childType = type;
            if(List.class.isAssignableFrom(type)){
                Class tempType = BeanKit.getParameterTypeClass(pd.getReadMethod().getGenericReturnType());
                if(tempType==null){
                    childType = Map.class;
                }else{
                    childType = tempType;
                }
            }

            Map<String,Object> childConfig = new HashMap<>();
			level++;
            getMappingByJson(configCol,nc,childConfig,childType,level);
			level--;
            configMap.put(attr,childConfig);
        }

        return ;
    }

    protected String getPrefix(String prefix,String col){
        if(prefix.length()==0){
            return col;
        }
        return prefix+"."+col;
    }


}
