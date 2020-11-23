package org.beetl.sql.core.mapping.join;

import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.ExecuteContext;

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

    protected static Map<AutoKey,AttrNode> cache = new ConcurrentHashMap<>();
    @Override
    protected AttrNode parse(ExecuteContext ctx, Class target, ResultSetMetaData rsmd, Annotation config) throws Exception {
        NameConversion nc = ctx.sqlManager.getNc();
        AutoKey key = new AutoKey(target,nc);
        AttrNode root = cache.get(key);
        if(root==null){
            Map columnIndex = this.getColumnIndex(rsmd);
            Map<String,Object> configMap = new CaseInsensitiveHashMap<>();
            String prefix = "";
            getMappingByJson(prefix,nc,configMap,target);
            root =  new AttrNode(null);
            root.initNode(target,configMap,columnIndex);
            cache.put(key,root);
        }
        return root;
    }

    protected void getMappingByJson(String prefix,NameConversion nc,Map<String,Object> configMap,Class target) throws IntrospectionException {
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
            getMappingByJson(configCol,nc,childConfig,childType);
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

    static class AutoKey {
        Class target;
        NameConversion nameConversion;

        public AutoKey(Class target, NameConversion nameConversion) {
            this.target = target;
            this.nameConversion = nameConversion;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AutoKey autoKey = (AutoKey) o;
            return target.equals(autoKey.target) &&
                    nameConversion.equals(autoKey.nameConversion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(target, nameConversion);
        }
    }
}
