package org.beetl.sql.core.orm;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLResultListener;
import org.beetl.sql.core.annotatoin.builder.BaseObjectBuilder;
import org.beetl.sql.core.annotatoin.builder.ObjectSelectBuilder;
import org.beetl.sql.core.db.ClassAnnotation;
import org.beetl.sql.core.kit.BeanKit;

/**
 * 
 * @author xiandafu
 *
 */
public class ORMObjectBuilder implements ObjectSelectBuilder {


	@Override
	public void beforeSelect(Class target, SQLManager sqlManager, Annotation beanAnnotaton, Map<String, Object> paras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Object> afterSelect(Class target, List<Object> entitys, SQLManager sqlManager, Annotation beanAnnotaton,
			SQLResult sqlResult) {
		if (target == null) {
            return entitys;
        }

        OrmQuery ormQuery = (OrmQuery) target.getAnnotation(OrmQuery.class);
        if (ormQuery == null) {
            return entitys;
        }

        OrmCondition[] condtions = ormQuery.value();

        Map<String, MappingEntity> map = new HashMap<String, MappingEntity>();

        for (OrmCondition cond : condtions) {
            MappingEntity mappingEntity = null;
            //类配合的orm查询总是
            if(cond.lazy()) {
                mappingEntity = new LazyMappingEntity();
            }else {
                mappingEntity = new MappingEntity();
            }
            mappingEntity.setSingle(cond.type() == OrmQuery.Type.ONE);
            mappingEntity.setTarget(cond.target().getName());
            if (cond.alias().length() != 0) {
                mappingEntity.setTailName(cond.alias());
            }

            mappingEntity.setSqlId(cond.sqlId().length() != 0 ? cond.sqlId() : null);
            Map<String, String> mapKey = new HashMap<String, String>();
            mapKey.put(cond.attr(), cond.targetAttr());
            mappingEntity.setMapkey(mapKey);
            map.put(mappingEntity.getTarget(), mappingEntity);


        }
        
        //增加到listener,统一后处理映射关系
        ORMSQLResultListener orm = new ORMSQLResultListener();
        orm.getMapingEntrys().addAll(map.values());
        List<SQLResultListener>  sqlResultListeners  =sqlResult.getListener();
       
        if (sqlResultListeners == null) {
        	sqlResultListeners = new  ArrayList<SQLResultListener> ();

        } 
        sqlResultListeners.add(orm);
        return  entitys;
	}
}
