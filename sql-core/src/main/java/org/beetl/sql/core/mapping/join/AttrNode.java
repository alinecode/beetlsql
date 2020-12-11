package org.beetl.sql.core.mapping.join;


import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.ReadTypeParameter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;

/**
 * 结果集ResultSet分析后得出的树，AttrNode代表了一个节点，包含了同层的所有属性，比如u.u_id, u.u_name,
 */
public   class AttrNode{
    /**
     * 节点对应的类型
     */
    public Class target;

    public AttrNode parent;
    /**
     * 子类，如果还有更深层次关系，比如当前在dept层，dept.concat是下一层
     */
    public List<AttrNode> children;

    public PropertyDescriptor typePdInParent;

    /**
     * ResultSet索引->基本属性
     */
    public Map<Integer,String> colMap = new HashMap<>();
    /**
     * 属性名->PropertyDescriptor
     */
    public Map<String,PropertyDescriptor> propertyMap = new HashMap<>();

    public AttrNode(){
        this.parent = null;
    }
    public AttrNode(AttrNode parent){
        this.parent = parent;
    }

    /**
     *
     * @param target 此节点类型
     * @param mapping 此节点的配置，属性-> 列名
     * @param columnIndexMap  列名->索引位置
     * @throws Exception
     */
    public void initNode(Class target,Map<String,Object> mapping,Map<String,Integer> columnIndexMap) throws Exception{
        this.target = target;
        for(Map.Entry<String,Object> entry:mapping.entrySet()){
            String attr = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof  String){
                String col = (String)value;
                Integer index = columnIndexMap.get(col);
                if(index==null){
                    continue;
//                        throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"配置错误 "+col);
                }
                colMap.put(index,attr);
                PropertyDescriptor pd = BeanKit.getPropertyDescriptor(target,attr);
                if(pd==null){
                    throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"映射配置错，未找到配置 col "+col+"对应的属性");
                }
                propertyMap.put(attr,pd);
                continue;
            }

            if(children==null){
                children = new ArrayList<>();
            }

            Map<String,Object> childMapping = (Map<String,Object>)value;

            PropertyDescriptor pd = BeanKit.getPropertyDescriptor(target,attr);
            Class type = pd.getPropertyType();
            AttrNode childNode = new AttrNode(this);
            childNode.typePdInParent = pd;
            if(Collection.class.isAssignableFrom(type)){
                Type genericReturnType =  pd.getReadMethod().getGenericReturnType();
                Class childTarget = BeanKit.getCollectionType(genericReturnType);
                if(childTarget==null){
                    //如果未提供泛型说明，则任何集合元素是Map
                    childTarget = Map.class;
                }
                childNode.initNode(childTarget,childMapping,columnIndexMap);
                children.add(childNode);
            }else{
                childNode.initNode(type,childMapping,columnIndexMap);
                children.add(childNode);
            }

        }
    }


    /**
     * 遍历同层的结果集，赋值到grid里
     * @param renderContext
     * @param ctx
     * @param rtp
     * @throws Exception
     */
    public  void visit (ConfigJoinMapper.RenderContext renderContext, ExecuteContext ctx, ReadTypeParameter rtp) throws Exception{
        ConfigJoinMapper.NodeValue nodeValue = getNodeValueFromResultSet(renderContext,ctx,rtp);
        ConfigJoinMapper.Grid grid = renderContext.grid;
        if(grid.contain(this,nodeValue.key)){
            //此数据已经被映成对象
            ConfigJoinMapper.ObjectWrapper realObj = grid.getNodeValue(this,nodeValue.key).objectWrapper;
			if(parent!=null){
				////赋值给父类
				ConfigJoinMapper.ObjectWrapper parentObj = renderContext.parent;
				assignToParent(renderContext,ctx,parentObj,realObj,this.typePdInParent);
			}
			//递归下一层
            renderContext.parent = realObj;
            visitAll(renderContext,ctx,rtp);
            return ;
        }else{
			//映射成新的实例
			grid.push(this,nodeValue);
			fillObject(nodeValue);
			//赋值给父类
			ConfigJoinMapper.ObjectWrapper realObj = nodeValue.objectWrapper;

			if(parent!=null){
				ConfigJoinMapper.ObjectWrapper parentObj = renderContext.parent;
				assignToParent(renderContext,ctx,parentObj,realObj,this.typePdInParent);
			}
			//遍历子节点
			renderContext.parent = realObj;
			visitAll(renderContext,ctx,rtp);
		}

    }

    //转化成对象
    void fillObject(ConfigJoinMapper.NodeValue nodeValue) throws Exception{

        ConfigJoinMapper.ObjectWrapper objectWrapper = new ConfigJoinMapper.ObjectWrapper();
        objectWrapper.fromNodeValue = nodeValue;
        objectWrapper.target = target;
        objectWrapper.makeObject( this.propertyMap);
    }
    //从ResultSet中获取值
    ConfigJoinMapper.NodeValue getNodeValueFromResultSet(ConfigJoinMapper.RenderContext renderContext, ExecuteContext ctx, ReadTypeParameter rtp) throws SQLException {
        if(colMap.isEmpty()){
            throw new IllegalArgumentException("无法映射对象"+this.target+" 没有结果集映射值,无法识别是否重复 ");
        }
        Map<String,Object> map = new HashMap<>();
        BeanProcessor beanProcessor = renderContext.beanProcessor;
        for(Map.Entry<Integer,String> entry:colMap.entrySet()){
            rtp.setIndex(entry.getKey());
            JavaSqlTypeHandler sqlTypeHandler = beanProcessor.getHandler(target);
            if(sqlTypeHandler==null){
                sqlTypeHandler = beanProcessor.getDefaultHandler();
            }
            Object value = sqlTypeHandler.getValue(rtp);
            String attr = entry.getValue();
            map.put(attr,value);
        }
        ConfigJoinMapper.NodeValue nodeValue = new ConfigJoinMapper.NodeValue(map);
        return nodeValue;
    }

    /**
     * 把当前节点渲染的值赋值给父对象,赋值之前，需要过滤，已经赋值过的无需再赋值
     * @param parent
     * @param attrValue
     * @param typePdInParent
     */
    void assignToParent(ConfigJoinMapper.RenderContext renderContext, ExecuteContext ctx
            , ConfigJoinMapper.ObjectWrapper parent, ConfigJoinMapper.ObjectWrapper attrValue, PropertyDescriptor typePdInParent) throws Exception{

        Class propertyType = typePdInParent.getPropertyType();
        if(List.class.isAssignableFrom(propertyType)||Set.class.isAssignableFrom(propertyType)){
            Set set =   parent.flagMap.get(typePdInParent);
            if(set==null){
                set = new HashSet();
                parent.flagMap.put(typePdInParent,set);
            }
            if(set.contains(attrValue.fromNodeValue.key)){
                //已经包含此值
                return ;
            }else{
                Collection values = (Collection)typePdInParent.getReadMethod().invoke(parent.realObject,new Object[0]);
                if(values==null){
                    values =  (Collection)BeanKit.newCollectionInstance(typePdInParent.getPropertyType());
                    typePdInParent.getWriteMethod().invoke(parent.realObject,values);
                }
                values.add(attrValue.realObject);

            }
            //标记nodeValue已经加过
            set.add(attrValue.fromNodeValue.key);
        }else{
            if(!parent.flagMap.containsKey(typePdInParent)){
                //未赋值，可以赋值给父对象了
                BeanProcessor beanProcessor = ctx.sqlManager.getDefaultBeanProcessors();
                beanProcessor.callSetter(parent.realObject, typePdInParent, attrValue.realObject, typePdInParent.getPropertyType());
            }
        }
    }

    public void visitAll(ConfigJoinMapper.RenderContext renderContext, ExecuteContext ctx, ReadTypeParameter rtp) throws Exception{
        if(children!=null){
            ConfigJoinMapper.ObjectWrapper wrapper = renderContext.parent;
            for(AttrNode node:children){
                node.visit(renderContext,ctx,rtp);
                renderContext.parent = wrapper;
            }
        }
    }
}

