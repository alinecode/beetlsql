package org.beetl.sql.core.orm;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLResultListener;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.annotatoin.builder.ObjectSelectBuilder;

/**
 * 
 * @author xiandafu
 *
 */
public class ORMObjectBuilder implements ObjectSelectBuilder {


	@Override
	public void beforeSelect(Class target, SQLScript sqlScript, Annotation beanAnnotaton, Map<String, Object> paras) {
		if (target == null||target==Map.class) {
            return ;
        }

        OrmQuery ormQuery = (OrmQuery) target.getAnnotation(OrmQuery.class);
        if (ormQuery == null) {
            return ;
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
        List<SQLResultListener>  sqlResultListeners  =sqlScript.getListener();
       
        if (sqlResultListeners == null) {
        	sqlResultListeners = new  ArrayList<SQLResultListener> ();
        } 
        
        sqlResultListeners.add(orm);
        sqlScript.setListener(sqlResultListeners);
		
	}

	@Override
	public List<Object> afterSelect(Class target, List<Object> entitys,  SQLScript sqlScript, Annotation beanAnnotaton,
			SQLResult sqlResult) {
		return entitys;
		
	}
}
