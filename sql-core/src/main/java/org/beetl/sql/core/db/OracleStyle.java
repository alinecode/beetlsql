package org.beetl.sql.core.db;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.meta.SchemaMetadataManager;
import org.beetl.sql.core.range.RangeSql;
import org.beetl.sql.core.range.RowNumRange;

import java.lang.annotation.Annotation;
import java.util.List;

public class OracleStyle extends AbstractDBStyle {

    RowNumRange rowNumRangeHelper = null;
    public OracleStyle() {
        rowNumRangeHelper = new RowNumRange(this);
    }


    @Override
    public int getIdType(Class c,String idProperty) {
    	 	List<Annotation> ans = BeanKit.getAllAnnotation(c, idProperty);
        int idType = DBType.ID_ASSIGN; // 默认是自增长

        for (Annotation an : ans) {
            if (an instanceof SeqID) {
                idType = DBType.ID_SEQ;
                //seq 总是优先
                break;
            } else if (an instanceof AssignID) {
                idType = DBType.ID_ASSIGN;
            }
        }

        return idType;

    }

    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    public int getDBType() {
        return DBType.DB_ORACLE;
    }

    @Override
    public RangeSql getRangeSql() {
        return rowNumRangeHelper;
    }

    @Override
    public String getSeqValue(String seqName) {
		return seqName+".nextval";
	}

	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs) {
		metadataManager = new SchemaMetadataManager(cs, this) {
			protected String[] getScope(String catalog,String schema){
				return new String[] { "TABLE","VIEW" ,"SYNONYM"};
			}
		};
		return metadataManager;
	}


}
