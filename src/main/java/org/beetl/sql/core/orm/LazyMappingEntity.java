package org.beetl.sql.core.orm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.db.ClassDesc;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.BeanKit;

/**
 * 实现关系映射,放入一个Lazy
 * 
 * @author xiandafu
 *
 */
public class LazyMappingEntity extends MappingEntity {

	@Override
	public void map(List list, SQLManager sm, Map paras) {
		if (list.size() == 0) {
			return;
		}
		init(list.get(0),sm.getEntityLoader());

		for (Object obj : list) {
			mapClassItem(obj, sm,paras);

		}

	}

	protected void mapClassItem(final Object obj, final SQLManager sm,Map sqlParas) {

		final String sqlManagerName = sm.getSQLManagerName();
		if (sqlId != null) {
			final Map<String, Object> paras = new HashMap<String, Object>();
			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = BeanKit.getBeanProperty(obj, attr);
				paras.put(targetAttr, value);

			}
			if(sqlParas!=null&&!sqlParas.isEmpty()) {
                //外部参数，非映射参数
                paras.putAll(sqlParas);
            }
			LazyEntity lazy = new LazyEntity() {

				@Override
				public Object get() {
					SQLManager sqlManager = getSQLManager(sqlManagerName);
					List ret = sqlManager.select(sqlId, targetClass, paras);
					return retValue(ret);
				}

			};
			setTailAttr(obj, lazy);

		} else {
			if (mapkey.size() == 1) {
				String tableName = sm.getNc().getTableName(targetClass);
				TableDesc tableDesc = sm.getMetaDataManager().getTable(tableName);
				ClassDesc classDesc = tableDesc.getClassDesc(targetClass, sm.getNc());
				if (classDesc.getIdAttrs().size() == 1 && classDesc.getIdAttrs().containsAll(mapkey.values())) {
					// 主键查询
					String foreignAttr = this.mapkey.keySet().iterator().next();
					final Object value = BeanKit.getBeanProperty(obj, foreignAttr);
					LazyEntity lazy = new LazyEntity() {

						@Override
						public Object get() {
							SQLManager sqlManager = getSQLManager(sqlManagerName);
							Object ret = sqlManager.single(targetClass, value);
							return ret;
						}

					};
					setTailAttr(obj, lazy);
					return;

				}
			}

			final Object ins = getIns(targetClass);
			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = BeanKit.getBeanProperty(obj, attr);
				BeanKit.setBeanProperty(ins, value, targetAttr);

			}
			LazyEntity lazy = new LazyEntity() {

				@Override
				public Object get() {
					SQLManager sqlManager = getSQLManager(sqlManagerName);
					List ret = sqlManager.template(ins);
					return retValue(ret);
				}

			};
			setTailAttr(obj, lazy);

		}

	}
	@Override
	protected void setTailAttr(Object o, Object value) {
        //懒加载只能通过tail接口来做了
        if (o instanceof Tail) {
            ((Tail) o).set(tailName, value);
        } else {
            // annotation
            Method m = BeanKit.getTailMethod(o.getClass());
            if (m == null) {
                throw new RuntimeException("懒加载 OR/Mapping 必须实现Tail接口,用于设置 "+tailName+"对象，或者不使用懒加载");
            }
            try {
                m.invoke(o, tailName, value);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    }

	private Object retValue(List ret) {
		if (isSingle) {
			if (ret.isEmpty()) {
				return null;
			} else {
				return ret.get(0);
			}

		} else {

			return ret;
		}
	}

	private SQLManager getSQLManager(String name) {
		return SQLManager.getSQLManagerByName(name);
	}

}
