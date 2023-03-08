package org.beetl.sql.core.mapping.join;


import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.ClassAnnotation;
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
 * 一个结果集映射规则可对应的树结构，一个AttrNode节点代表了一个JavaBean的所有属性与对应映射的数据库表的列关系。
 * 可与通过运行 S2MappingSample的jsonConfig来调试理解过程，想象一下一个JavaBean的属性实质上也能构成一个树的数据结构。
 */
public class AttrNode {
	/**
	 * 当前节点对应的JavaBean类型。
	 * <br/>
	 * 如果字段是非集合类型，值就是字段的类型。
	 * <br/>
	 * 如果字段是集合类型，值取决集合是否给定了泛型；如果泛型确定，则是泛型类型，如果未知，则默认是Map
	 */
	public Class<?> target;

	protected boolean isCollection;

	public AttrNode parent;
	/**
	 * 当前对应的JavaBean中的其它引用类型的节点
	 */
	public List<AttrNode> children;

	/**
	 * 当前节点对应Bean所在的父级Bean属性的PropertyDescriptor
	 */
	public PropertyDescriptor typePdInParent;

	/**
	 * ResultSet列索引->对应字段属性名
	 */
	public Map<Integer, String> colMap = new HashMap<>();

	Map<String, AttributeConvert> attrConvertMap = new HashMap<>();

	/**
	 * 属性名->字段的PropertyDescriptor
	 */
	public Map<String, PropertyDescriptor> propertyMap = new HashMap<>();

	public AttrNode() {
		this.parent = null;
	}

	public AttrNode(AttrNode parent) {
		this.parent = parent;
	}

	/**
	 *
	 * @param target 此节点类型
	 * @param jsonMapping 当前的json映射规则配置，{属性(key):列名(value)}
	 * @param columnIndexMap  ResultSet中 列名(key)->索引位置(value) 的记录
	 */
	public void initNode(Class<?> target, Map<String, Object> jsonMapping, Map<String, Integer> columnIndexMap) {
		this.target = target;
		ClassAnnotation an = ClassAnnotation.getClassAnnotation(target);

		if (an.isContainExtAnnotation()) {
			attrConvertMap = an.getExtAnnotation().getAttributeConvertMap();
		}
		for (Map.Entry<String, Object> entry : jsonMapping.entrySet()) {
			String attr = entry.getKey();
			Object value = entry.getValue();
			PropertyDescriptor pd = BeanKit.getPropertyDescriptor(target, attr);
			if (pd == null) {
				throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "字段[" + attr + "]映射规则错误，实体类无该段");
			}
			if (value instanceof String) {
				String col = (String) value;
				Integer index = columnIndexMap.get(col);
				if (index == null) {
					/*在同一个映射下，每次查询中不一定都会用上所有的映射规则列*/
					continue;
				}
				colMap.put(index, attr);
				propertyMap.put(attr, pd);
				continue;
			}

			if (children == null) {
				children = new ArrayList<>();
			}

			Map<String, Object> childMapping = (Map<String, Object>) value;

			Class<?> type = pd.getPropertyType();
			AttrNode childNode = new AttrNode(this);
			childNode.typePdInParent = pd;
			if (Collection.class.isAssignableFrom(type)) {
				this.isCollection = true;
				/*如果是集合，则获取集合的泛型*/
				Type genericReturnType = pd.getReadMethod().getGenericReturnType();
				Class childTarget = BeanKit.getCollectionType(genericReturnType);
				if (childTarget == null) {
					//如果未提供泛型说明，则任何集合元素是Map
					childTarget = Map.class;
				}
				childNode.initNode(childTarget, childMapping, columnIndexMap);
				children.add(childNode);
			} else {
				childNode.initNode(type, childMapping, columnIndexMap);
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
	public void visit(ConfigJoinMapper.RenderContext renderContext, ExecuteContext ctx, ReadTypeParameter rtp)
			throws Exception {
		ConfigJoinMapper.NodeValue nodeValue = getNodeValueFromResultSet(renderContext, ctx, rtp);
		if (!nodeValue.isValid) {
			return;
		}

		ConfigJoinMapper.Grid grid = renderContext.grid;
		if (grid.contain(this, nodeValue.key)) {
			//此数据已经被映成对象
			ConfigJoinMapper.ObjectWrapper realObj = grid.getNodeValue(this, nodeValue.key).objectWrapper;
			if (parent != null) {
				//赋值给父类
				ConfigJoinMapper.ObjectWrapper parentObj = renderContext.parent;
				assignToParent(renderContext, ctx, parentObj, realObj, this.typePdInParent);
			}
			//递归下一层
			renderContext.parent = realObj;
			visitAll(renderContext, ctx, rtp);
			return;
		} else {
			//映射成新的实例
			grid.push(this, nodeValue);
			fillObject(nodeValue);
			//赋值给父类
			ConfigJoinMapper.ObjectWrapper realObj = nodeValue.objectWrapper;

			if (parent != null) {
				ConfigJoinMapper.ObjectWrapper parentObj = renderContext.parent;
				assignToParent(renderContext, ctx, parentObj, realObj, this.typePdInParent);
			}
			//遍历子节点
			renderContext.parent = realObj;
			visitAll(renderContext, ctx, rtp);
		}

	}

	//转化成对象
	void fillObject(ConfigJoinMapper.NodeValue nodeValue) throws Exception {

		ConfigJoinMapper.ObjectWrapper objectWrapper = new ConfigJoinMapper.ObjectWrapper();
		objectWrapper.fromNodeValue = nodeValue;
		objectWrapper.target = target;
		objectWrapper.makeObject(this.propertyMap);
	}

	/**
	 * 从结果集得到节点中所有的字段对应的列的值
	 *
	 * @param renderContext 渲染上下文
	 * @param ctx ctx
	 * @param rtp rtp
	 * @return {@link org.beetl.sql.core.mapping.join.ConfigJoinMapper.NodeValue}
	 * @throws SQLException sqlexception异常
	 */
	ConfigJoinMapper.NodeValue getNodeValueFromResultSet(ConfigJoinMapper.RenderContext renderContext,
			ExecuteContext ctx, ReadTypeParameter rtp) throws SQLException {
		if (colMap.isEmpty()) {
			//无配置，则不映射
			return new ConfigJoinMapper.NodeValue(false);
		}
		Map<String, Object> map = new HashMap<>();
		BeanProcessor beanProcessor = renderContext.beanProcessor;
		boolean allNull = true;
		for (Map.Entry<Integer, String> entry : colMap.entrySet()) {
			String attr = entry.getValue();
			rtp.setIndex(entry.getKey());

			AttributeConvert attributeConvert = attrConvertMap.get(attr);
			if(attributeConvert!=null){
				Object value = attributeConvert.toAttr(ctx,this.target,attr,rtp.rs,rtp.index);
				map.put(attr, value);
				continue;
			}

			PropertyDescriptor ps = propertyMap.get(entry.getValue());
			Class propertyType = ps.getPropertyType();
			JavaSqlTypeHandler sqlTypeHandler = beanProcessor.getHandler(propertyType);
			if (sqlTypeHandler == null) {
				sqlTypeHandler = beanProcessor.getDefaultHandler();
			}
			Object value = sqlTypeHandler.getValue(rtp);
			if (value != null) {
				allNull = false;
			}


			map.put(attr, value);

		}
		//如果当前层级的值都是null，则不创建当前层级对象
		if (allNull) {
			return new ConfigJoinMapper.NodeValue(false);
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
	void assignToParent(ConfigJoinMapper.RenderContext renderContext, ExecuteContext ctx,
			ConfigJoinMapper.ObjectWrapper parent, ConfigJoinMapper.ObjectWrapper attrValue,
			PropertyDescriptor typePdInParent) throws Exception {

		Class propertyType = typePdInParent.getPropertyType();
		if (List.class.isAssignableFrom(propertyType) || Set.class.isAssignableFrom(propertyType)) {
			Set set = parent.flagMap.get(typePdInParent);
			if (set == null) {
				set = new HashSet();
				parent.flagMap.put(typePdInParent, set);
			}
			if (set.contains(attrValue.fromNodeValue.key)) {
				//已经包含此值
				return;
			} else {
				Collection values = (Collection) typePdInParent.getReadMethod()
						.invoke(parent.realObject, new Object[0]);
				if (values == null) {
					values = BeanKit.newCollectionInstance(typePdInParent.getPropertyType());
					typePdInParent.getWriteMethod().invoke(parent.realObject, values);
				}
				values.add(attrValue.realObject);

			}
			//标记nodeValue已经加过
			set.add(attrValue.fromNodeValue.key);
		} else {
			if (!parent.flagMap.containsKey(typePdInParent)) {
				//未赋值，可以赋值给父对象了
				BeanProcessor beanProcessor = ctx.sqlManager.getDefaultBeanProcessors();
				beanProcessor.callSetter(parent.realObject, typePdInParent, attrValue.realObject,
						typePdInParent.getPropertyType());
			}
		}
	}

	public void visitAll(ConfigJoinMapper.RenderContext renderContext, ExecuteContext ctx, ReadTypeParameter rtp)
			throws Exception {
		if (children != null) {
			ConfigJoinMapper.ObjectWrapper wrapper = renderContext.parent;
			for (AttrNode node : children) {
				node.visit(renderContext, ctx, rtp);
				renderContext.parent = wrapper;
			}
		}
	}

}

