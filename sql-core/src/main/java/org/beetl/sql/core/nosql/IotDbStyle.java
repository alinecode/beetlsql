package org.beetl.sql.core.nosql;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.BooleanTypeHandler;
import org.beetl.sql.core.mapping.type.DoubleTypeHandler;
import org.beetl.sql.core.mapping.type.ReadTypeParameter;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.range.LimitWithOffsetRange;
import org.beetl.sql.core.range.RangeSql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 数据库差异：iotdb,目前itodjbc实现比较奇怪，因此参考IotDbHackStyle：
 * ResultSet.wasNull 方法会抛出不支持的异常，因为无法判断是否为null，如果为null，调用任务resetSet.getXXX方法
 * 都会抛出null异常
 * 因此使用的时候，目前的iotdb，请避免数据有null的情况,等待支持wasNull方法（IoTDBRpcDataSet.isNull已经支持，但不知为何不使用)
 * 目前 JDBC驱动有太多的不成熟地方
 * @author xiandafu
 *
 */
@Deprecated
public class IotDbStyle extends AbstractDBStyle {

    RangeSql rangeSql = null;
    public IotDbStyle() {
        rangeSql = new LimitWithOffsetRange(this);
        this.keyWordHandler = null;
    }

    @Override
    public int getIdType(Class c,String idProperty) {
        return DBType.ID_ASSIGN;
    }

	@Override
	public  boolean metadataSupport(){
		return false ;
	}

	@Override
	public  boolean preparedStatementSupport(){
		return false ;
	}

	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs){
		metadataManager = new NoSchemaMetaDataManager();
		return metadataManager;
	}

    @Override
    public String getName() {
        return "iotdb";
    }

    @Override
    public int getDBType() {
        return DBType.DB_IOTDB;
    }

    @Override
    public RangeSql getRangeSql() {
//    	select count(*) 会返回多列，目前没有办法翻页查询，真能自己写了
    	throw new UnsupportedOperationException();
//       return rangeSql;
    }

	@Override
	public String wrapStatementValue(Object value){
		if(preparedStatementSupport()){
			throw new IllegalStateException("支持jdbc PreparedStatement，优先使用PreparedStatement提高性能，保证安全");
		}
		if (value == null) {
			return "null";
		}
		if (value instanceof String) {
			return "\"" + value + "\"";
		} else if (value instanceof Number) {
			return value.toString();
		} else if (value instanceof java.sql.Timestamp) {
			return ((java.sql.Timestamp)value).getTime()+"";
		} else if (value instanceof java.sql.Date) {
			return ((java.sql.Date)value).getTime()+"";
		} else if (value instanceof java.util.Date) {
			return ((java.util.Date)value).getTime()+"";
		}else if (value instanceof Boolean) {
			return ((Boolean)value).toString();
		}
		else {
			throw new IllegalArgumentException("不支持类型 " + value.getClass() + "," + value);
		}

	}



	@Override
	public void config(SQLManager sqlManager){

		sqlManager.setDefaultBeanProcessors( new BeanProcessor(){
			@Override
			protected  String getColName(ExecuteContext ctx, ResultSetMetaData rsmd, int col) throws SQLException{
				/*iotdb会返回STORAGE GROUP，需要排除*/
				String colName = super.getColName(ctx,rsmd,col);
				int index = colName.lastIndexOf('.');
				if(index==-1){
					return colName;
				}else{
					return colName.substring(index+1);
				}

			}
		});

		BeanProcessor beanProcessor = sqlManager.getDefaultBeanProcessors();
		beanProcessor.addHandler(Boolean.class,new IotDbBooleanTypeHandler());
		beanProcessor.addHandler(boolean.class,new IotDbBooleanTypeHandler());
		beanProcessor.addHandler(Double.class,new IotDoubleTypeHandler());
		beanProcessor.addHandler(double.class,new IotDoubleTypeHandler());
	}

	/**
	 * 忽略null判断
	 */
	public class IotDbBooleanTypeHandler extends BooleanTypeHandler {

		@Override
		public Object getValue(ReadTypeParameter typePara) throws SQLException {
			ResultSet rs = typePara.rs;
			boolean a = rs.getBoolean(typePara.index);
			return a;
		}

	}

	public class IotDoubleTypeHandler extends DoubleTypeHandler  {

		@Override
		public Object getValue(ReadTypeParameter typePara) throws SQLException {
			ResultSet rs = typePara.rs;

			double a = rs.getDouble(typePara.index);
			return a ;

		}

	}


}
