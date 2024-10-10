package org.beetl.sql.jmh.sqltutils;

import org.beetl.sql.jmh.base.BaseService;
import org.beetl.sql.jmh.base.DataSourceHelper;
import org.beetl.sql.jmh.sqltutils.model.SQLSysUser;
import org.noear.solon.data.sql.Row;
import org.noear.solon.data.sql.SqlUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SqlUtilsService implements BaseService {
	AtomicInteger idGen = new AtomicInteger(1000);

	SqlUtils db;

	public void init() {
		DataSource dataSource = DataSourceHelper.newDatasource();

		this.db = SqlUtils.of(dataSource);
	}


	@Override
	public void addEntity() {
		SQLSysUser sqlSysUser = new SQLSysUser();
		sqlSysUser.setId(idGen.getAndIncrement());
		sqlSysUser.setCode("abc");
		sqlSysUser.setCode1("abc");
		sqlSysUser.setCode2("abc");
		sqlSysUser.setCode3("abc");
		sqlSysUser.setCode4("abc");
		sqlSysUser.setCode5("abc");
		sqlSysUser.setCode6("abc");
		sqlSysUser.setCode7("abc");
		sqlSysUser.setCode8("abc");
		sqlSysUser.setCode9("abc");
		sqlSysUser.setCode10("abc");
		sqlSysUser.setCode11("abc");
		sqlSysUser.setCode12("abc");
		sqlSysUser.setCode13("abc");
		sqlSysUser.setCode14("abc");
		sqlSysUser.setCode15("abc");
		sqlSysUser.setCode16("abc");
		sqlSysUser.setCode17("abc");
		sqlSysUser.setCode18("abc");
		sqlSysUser.setCode19("abc");
		sqlSysUser.setCode20("abc");


		try {
			db.insert("insert into sys_user  (id,code,code1,code2,code3,code4,code5,code6,code7,code8,code9,code10,code11,code12,code13,code14,code15,code16,code17,code18,code19,code20) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				sqlSysUser.getId(), sqlSysUser.getCode(), sqlSysUser.getCode1(),
				sqlSysUser.getCode2(), sqlSysUser.getCode3(), sqlSysUser.getCode4(),
				sqlSysUser.getCode5(), sqlSysUser.getCode6(), sqlSysUser.getCode7(),
				sqlSysUser.getCode8(), sqlSysUser.getCode9(), sqlSysUser.getCode10(),
				sqlSysUser.getCode11(), sqlSysUser.getCode12(), sqlSysUser.getCode13(),
				sqlSysUser.getCode14(), sqlSysUser.getCode15(), sqlSysUser.getCode16(),
				sqlSysUser.getCode17(), sqlSysUser.getCode18(), sqlSysUser.getCode19(),
				sqlSysUser.getCode20());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public Object getEntity() {
		try {
			Row row = db.selectRow("select * from sys_user where id=?", 1);

			return bind(row);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void lambdaQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void executeJdbcSql() {
		try {
			db.selectRow("select * from sys_user limit 1");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void executeTemplateSql() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sqlFile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void one2Many() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pageQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void complexMapping() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getAll() {
		ArrayList<SQLSysUser> sqlSysUsers = new ArrayList<>();

		try {
			List<Row> list = db.selectRowList("select * from sys_user");
			for (Row row : list) {
				sqlSysUsers.add(bind(row));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	SQLSysUser bind(Row row) throws SQLException {
		SQLSysUser sqlSysUser = new SQLSysUser();

		sqlSysUser.setId((Integer) row.getValue(1));
		sqlSysUser.setCode((String) row.getValue(2));
		sqlSysUser.setCode1((String) row.getValue(3));
		sqlSysUser.setCode2((String) row.getValue(4));
		sqlSysUser.setCode3((String) row.getValue(5));
		sqlSysUser.setCode4((String) row.getValue(6));
		sqlSysUser.setCode5((String) row.getValue(7));
		sqlSysUser.setCode6((String) row.getValue(8));
		sqlSysUser.setCode7((String) row.getValue(9));
		sqlSysUser.setCode8((String) row.getValue(10));
		sqlSysUser.setCode9((String) row.getValue(11));
		sqlSysUser.setCode10((String) row.getValue(12));
		sqlSysUser.setCode11((String) row.getValue(13));
		sqlSysUser.setCode12((String) row.getValue(14));
		sqlSysUser.setCode13((String) row.getValue(15));
		sqlSysUser.setCode14((String) row.getValue(16));
		sqlSysUser.setCode15((String) row.getValue(17));
		sqlSysUser.setCode16((String) row.getValue(18));
		sqlSysUser.setCode17((String) row.getValue(19));
		sqlSysUser.setCode18((String) row.getValue(20));
		sqlSysUser.setCode19((String) row.getValue(21));
		sqlSysUser.setCode20((String) row.getValue(22));

		return sqlSysUser;
	}

	//test
	public static void main(String[] args) {
		SqlUtilsService service = new SqlUtilsService();
		service.init();

		service.getEntity();
	}
}
