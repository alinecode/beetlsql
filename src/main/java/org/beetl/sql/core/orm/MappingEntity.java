package org.beetl.sql.core.orm;

import org.beetl.core.exception.BeetlException;
import org.beetl.core.fun.MethodInvoker;
import org.beetl.core.fun.PojoMethodInvoker;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.db.ClassDesc;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
import org.beetl.sql.core.kit.StringKit;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;


/**
 * 实现关系映射
 * @author xiandafu
 *
 */
public class MappingEntity implements java.io.Serializable {
	protected String target;
	protected boolean isSingle = false;
	protected Map<String, String> mapkey;
	protected String sqlId = null;
	//暂时不支持
	protected Map<String, Object> queryParas = null;

	//
	protected String tailName;
	protected boolean absentPackage = false;
	protected Class targetClass = null;


	Map<String, List> cache = new HashMap<String, List>();

	public void singleMap(Object o, SQLManager sm) {
		this.map(Arrays.asList(o), sm, Collections.EMPTY_MAP);
	}

	public void map(List list, SQLManager sm, Map paras) {
		if (list.size() == 0) {
			return;
		}
		init(list.get(0), sm.getEntityLoader());
		if (mapkey.size() == 1) {
			//有可能是主键映射
			String tableName = sm.getNc().getTableName(targetClass);
			TableDesc tableDesc = sm.getMetaDataManager().getTable(tableName);
			ClassDesc classDesc = tableDesc.getClassDesc(targetClass, sm.getNc());
			if (classDesc.getIdAttrs().size() == 1 && classDesc.getIdAttrs().containsAll(mapkey.values())) {
				//一次查询所有
				allInOneQuery(list, tableDesc, classDesc, sm);
				return;

			} else {
				//使用下面的普通查询,普通查询也用了缓存，性能也会提高
			}
		}


		for (Object obj : list) {
			mapClassItem(obj, sm, paras);

		}

	}

	private void allInOneQuery(List list, TableDesc tableDesc, ClassDesc classDesc, SQLManager sm) {
		String idAttr = classDesc.getIdAttrs().get(0);

		String idCol = ((CaseInsensitiveOrderSet) tableDesc.getIdNames()).getFirst();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(tableDesc.getSchema() == null ? "" : tableDesc.getSchema() + ".")
				.append(tableDesc.getName()).append(" where ").append(idCol).append(" in (");
		Set<Object> idValues = new HashSet<Object>(list.size());
		String foreignAttr = this.mapkey.keySet().iterator().next();
		for (Object o : list) {
			Object id = BeanKit.getBeanProperty(o, foreignAttr);
			idValues.add(id);

		}
		for (Object id : idValues) {
			sb.append("?,");

		}

		sb.setLength(sb.length() - 1);
		sb.append(")");
		//合并成一条查询
		SQLReady ready = new SQLReady(sb.toString(), idValues.toArray());
		List rets = sm.execute(ready, targetClass);
		Map<Object, Object> mapRets = new HashMap<Object, Object>();
		for (Object ret : rets) {
			Object id = BeanKit.getBeanProperty(ret, idAttr);
			mapRets.put(id, ret);
		}
		//赋值给list里完成映射		
		for (Object o : list) {
			Object foreignId = BeanKit.getBeanProperty(o, foreignAttr);
			Object ref = mapRets.get(foreignId);
			if (this.isSingle) {
				setTailAttr(o, ref);
			} else {
				List listRet = new ArrayList(1);
				listRet.add(ref);
				setTailAttr(o, listRet);
			}


		}


	}

	protected void init(Object obj, ClassLoader loader) {
		if (target.indexOf(".") == -1) {
			absentPackage = true;


		}

		if (this.tailName == null) {
			if (target.indexOf(".") == -1) {
				this.tailName = StringKit.toLowerCaseFirstOne(target);
			} else {
				int index = target.lastIndexOf(".");
				String className = target.substring(index + 1);
				this.tailName = StringKit.toLowerCaseFirstOne(className);
			}
		}
		//缺少包名,则认为是跟关系对象同一个包名
		String fullName = absentPackage ? BeanKit.getPackageName(obj.getClass()) + "." + target : target;
		targetClass = getCls(fullName, loader);
	}


	protected void mapClassItem(Object obj, SQLManager sm, Map sqlParas) {
		List ret = null;
		StringBuilder key = new StringBuilder();
		if (sqlId != null) {
			Map<String, Object> paras = new HashMap<String, Object>();
			if (sqlParas != null && !sqlParas.isEmpty()) {
				//外部参数，非映射参数
				paras.putAll(sqlParas);
			}

			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = BeanKit.getBeanProperty(obj, attr);
				paras.put(targetAttr, value);
				key.append(value).append("_");

			}

			String cacheKey = key.toString();
			if (cache.containsKey(cacheKey)) {
				ret = cache.get(cacheKey);
			} else {
				ret = sm.select(sqlId, targetClass, paras);
				cache.put(cacheKey, ret);
			}

		} else {

			Object ins = getIns(targetClass);
			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = BeanKit.getBeanProperty(obj, attr);
				BeanKit.setBeanProperty(ins, value, targetAttr);
				key.append(value).append("_");

			}


			String cacheKey = key.toString();
			if (cache.containsKey(cacheKey)) {
				ret = cache.get(cacheKey);
			} else {
				ret = sm.template(ins);
				cache.put(cacheKey, ret);
			}
		}

		if (this.isSingle) {
			if (ret.isEmpty()) {
				setTailAttr(obj, null);
			} else {
				setTailAttr(obj, ret.get(0));
			}


		} else {

			setTailAttr(obj, ret);
		}
	}


	protected void setTailAttr(Object o, Object value) {

		MethodInvoker setter = BeanKit.getMethodInvokerProperty(o, tailName);

		if (setter != null && setter instanceof PojoMethodInvoker) {
			try {
				setter.set(o, value);
			} catch (BeetlException ex) {
				throw new RuntimeException(ex);
			}

		} else if (o instanceof Tail) {
			((Tail) o).set(tailName, value);
		} else {
			// annotation
			Method m = BeanKit.getTailMethod(o.getClass());
			if (m == null) {
				throw new RuntimeException(
						"OR/Mapping 找不到对应的setter方法:" + tailName + ",请设置setter方法或者实现Tail接口，采用@Tail注解");
			}
			try {
				m.invoke(o, tailName, value);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

		}
	}

	protected Object getIns(Class cls) {
		try {

			return cls.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	protected Class getCls(String fullName, ClassLoader loader) {
		try {
			if (loader != null) {
				return loader.loadClass(fullName);
			}
			loader = Thread.currentThread().getContextClassLoader();
			if (loader != null) {
				return loader.loadClass(fullName);
			} else {
				return Class.forName(fullName);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}


	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {

		this.target = target;
	}

	public boolean isSingle() {
		return isSingle;
	}

	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}

	public Map<String, String> getMapkey() {
		return mapkey;
	}

	public void setMapkey(Map<String, String> mapkey) {
		this.mapkey = mapkey;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public String getTailName() {
		return tailName;
	}

	public void setTailName(String tailName) {
		this.tailName = tailName;
	}

	public Map<String, Object> getQueryParas() {
		return queryParas;
	}

	public void setQueryParas(Map<String, Object> queryParas) {
		this.queryParas = queryParas;
	}

}
