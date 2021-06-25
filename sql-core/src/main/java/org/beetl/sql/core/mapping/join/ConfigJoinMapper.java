package org.beetl.sql.core.mapping.join;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.ResultSetMapper;
import org.beetl.sql.core.mapping.type.ReadTypeParameter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通过配置来实现映射，配置可以是任意格式，比如json或者xml，或者其他格式，子类实现parse方法将这些特定格式配置转化为{@link AttrNode}
 *
 * <pre>@{
 * @ResultProvider(JsonConfigJoinMapper.class)
 * @JsonMapper("{'id':'id','name':'name','users':{'id':'u_id','name':'u_name'}}")
 * public class MyDepartment {
 *     Integer id;
 *     String name;
 *     List<MyUser>  users;
 * }
 *
 * }</pre>
 *
 * <pre>
 *   SQLReady ready = new SQLReady("select d.id id,d.name name ,u.id u_id,u.name u_name
 *      from department d join user u on d.id=u.department_id ");
 *   List<MyDepartment> list = sqlManager.execute(ready,MyDepartment.class);
 *
 * </pre>
 * {@code AttrNode},AttrNode实现了visit模式，通过遍历ResultSet获得结果集
 *
 * 需要注意的，目前任何ConfigMapper实现映射，都需要确保树节点每层都有值，因为JsonConfigMapper会依据这些值作为key
 * 确保不会重建对象。如下是合法的，因为第一层是id和name
 * <pre>
 *     select d.id id,d.name name ,u.id `u.id`,u.name `u.name`
 * </pre>
 *
 * 这个是不合法的,因为第一层无值
 * <pre>
 *     select d.id `d.id` ,d.name 'd.name' ,u.id u_id,u.name u_name
 * </pre>
 *
 * 参考 ${link AttrNode#getNodeValueFromResultSet}
 *
 * @author xiandafu
 * @author 一日看尽长安花
 * @see  org.beetl.sql.annotation.entity.ResultProvider
 * @see JsonMappingTest
 */
public abstract class ConfigJoinMapper implements ResultSetMapper {


    @Override
    public List mapping(ExecuteContext ctx, Class target, ResultSet resultSet, Annotation config) {

        try{
            ResultSetMetaData resultSetMetaData =  resultSet.getMetaData();
            AttrNode root =  parse(ctx,target, resultSetMetaData,config);
            ReadTypeParameter rtp = new ReadTypeParameter(ctx.sqlId,ctx.sqlManager.getDbStyle().getName(),target,resultSet,resultSetMetaData,1,ctx);
            RenderContext renderContext = new RenderContext();
            renderContext.beanProcessor = ctx.sqlManager.getDefaultBeanProcessors();
            while(resultSet.next()){
                root.visit(renderContext,ctx,rtp);
            }
            if(renderContext.grid.gridValues.isEmpty()){
                return new ArrayList();
            }
            List<NodeValue> nodeValues = renderContext.grid.getAll(root);

            return nodeValues.stream().map(nodeValue ->
                    nodeValue.objectWrapper.realObject)
                    .collect(Collectors.toList());

        }catch(SQLException ex){
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,ex);
        }catch(Exception ex){
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,ex);
        }
    }
    protected  Map<String,Integer> getColumnIndex(ResultSetMetaData rsmd) throws SQLException{
        int count = rsmd.getColumnCount();
        Map<String,Integer>  map =  new CaseInsensitiveHashMap();
        for(int col=1;col<=count;col++){
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(col);
            }
            map.put(columnName,col);
        }
        return map;
    }


    /**
     * 子类继承,根据映射配置获得一个树结构,需要考虑缓存以避免每次解析
     * @param target
     * @param rsmd
     * @return
     * @throws SQLException
     */
    abstract protected AttrNode parse(ExecuteContext ctx,Class target,ResultSetMetaData rsmd,Annotation config)  throws Exception;


    static class RenderContext{
        Grid grid = new Grid();
        ObjectWrapper parent = null;
        BeanProcessor beanProcessor = null;
    }

    /**
     * 执行节点得到一堆值
     */
    static class NodeValue{
        Object key;
        Map<String,Object> value;
        //value对应的真实对象
        ObjectWrapper objectWrapper;

        /**
         * 如果无key，则是使用属性本生
         * @param values
         */
       public NodeValue(Map values){
           //如果没有key，则使用一个临时key
           this.key = new TempKey(values);
           this.value = values;
       }

        public NodeValue(Object key, Map values){
            this.key = key;
            this.value=values;
        }

        static class TempKey{
            Map<String,Object> value;
            int hashCode;

            public TempKey(Map<String,Object> value){
                this.value = value;
                hashCode = value.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                TempKey tempKey = (TempKey) o;
                return value.equals(tempKey.value);
            }

            @Override
            public int hashCode() {
                return hashCode;
            }
        }
    }

    /**
     * 遍历节点后得到的NodeValue，然后转化为ObjectWrapper，里面包含了实际生成的对象
     */
    static class ObjectWrapper{
        Class target;
        Object realObject;
        NodeValue fromNodeValue;
        /**
         * 标记此属性已经具备的NodeValue，避免重复加入到此对象
         */
        Map<PropertyDescriptor,Set> flagMap = new HashMap<>();

        public void makeObject(Map<String,PropertyDescriptor> propertyMap) throws Exception{
            if(Map.class.isAssignableFrom(target)){
                realObject = fromNodeValue.value;
            }else{
                //copy成对象
                Object obj = BeanKit.newInstance(target);
                Map<String,Object> valueMap = fromNodeValue.value;
                for(Map.Entry<String,Object> entry:valueMap.entrySet()){
                    String attr = entry.getKey();
                    Object value = entry.getValue();
                    PropertyDescriptor ps = propertyMap.get(attr);
                    try{
                        ps.getWriteMethod().invoke(obj,value);
                    }catch(IllegalArgumentException illegalArgumentException){
                        throw new IllegalArgumentException(ps+" 属性不匹配 "+ps.getName()+" for "+value.getClass());
                    }


                }
                this.realObject = obj;
            }
            fromNodeValue.objectWrapper = this;
        }
    }

    /**
     * 保存遍历ResultSet后的了Node执行生成的值，可以理解为缓存。
     */
    static class Grid{

        Map<AttrNode,Set<Object>> gridKeys = new HashMap<>();
        //node执行后生成的值
        Map<AttrNode,List<NodeValue>>  gridValues = new HashMap<>();
        public void push(AttrNode node,NodeValue nodeValue){
            Set<Object> cell =  gridKeys.get(node);
            if(cell==null){
                cell = new HashSet<>();
                gridKeys.put(node,cell);
                gridValues.put(node,new ArrayList<>());
            }
            if(!cell.contains(nodeValue.key)){
                cell.add(nodeValue.key);
                List<NodeValue> nodeValueArrayList = gridValues.get(node);
                nodeValueArrayList.add(nodeValue);
            }

        }


        public boolean contain(AttrNode node,Object key){
            Set<Object> set =   gridKeys.get(node);

            if(set==null){
                return false;
            }
            return set.contains(key);

        }

        public NodeValue getNodeValue(AttrNode node,Object key){
            List<NodeValue> cell =  gridValues.get(node);
            if(cell==null){
                return null;
            }
            //TODO 效率提升
            for(NodeValue nodeValue:cell){
                if(nodeValue.key.hashCode()==key.hashCode()&&nodeValue.key.equals(key)){
                    return nodeValue;
                }
            }
            return null;

        }

        public List<NodeValue> getAll(AttrNode node){
            List<NodeValue> cell =  gridValues.get(node);
            return cell;
        }
    }


}
