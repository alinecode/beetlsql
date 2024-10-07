package org.beetl.sql.jmh.sqltutils;

import org.beetl.sql.jmh.base.BaseService;
import org.beetl.sql.jmh.base.DataSourceHelper;
import org.beetl.sql.jmh.sqltutils.model.SQLSysUser;
import org.noear.solon.Solon;
import org.noear.solon.data.sql.SqlUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SqlUtilsService implements BaseService {
	AtomicInteger idGen = new AtomicInteger(1000);

	SqlUtils db;

	public void init() {
		Solon.start(SqlUtilsService.class, new String[0]);
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
			Map<String, Object> map = db.selectRow("select * from sys_user where id=?", 1);

			return bind(map);
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

		Iterator<Map<String, Object>> iterator = null;
		try {
			iterator = db.selectRowStream("select * from sys_user", 100);
			while (iterator.hasNext()) {
				sqlSysUsers.add(bind(iterator.next()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (iterator != null) {
				iterator.remove();
			}
		}
	}

	SQLSysUser bind(Map<String, Object> map) {
		SQLSysUser sqlSysUser = new SQLSysUser();

		sqlSysUser.setId((Integer) map.get("id"));
		sqlSysUser.setCode((String) map.get("code"));
		sqlSysUser.setCode1((String) map.get("code1"));
		sqlSysUser.setCode2((String) map.get("code2"));
		sqlSysUser.setCode3((String) map.get("code3"));
		sqlSysUser.setCode4((String) map.get("code4"));
		sqlSysUser.setCode5((String) map.get("code5"));
		sqlSysUser.setCode6((String) map.get("code6"));
		sqlSysUser.setCode7((String) map.get("code7"));
		sqlSysUser.setCode8((String) map.get("code8"));
		sqlSysUser.setCode9((String) map.get("code9"));
		sqlSysUser.setCode10((String) map.get("code10"));
		sqlSysUser.setCode11((String) map.get("code11"));
		sqlSysUser.setCode12((String) map.get("code12"));
		sqlSysUser.setCode13((String) map.get("code13"));
		sqlSysUser.setCode14((String) map.get("code14"));
		sqlSysUser.setCode15((String) map.get("code15"));
		sqlSysUser.setCode16((String) map.get("code16"));
		sqlSysUser.setCode17((String) map.get("code17"));
		sqlSysUser.setCode18((String) map.get("code18"));
		sqlSysUser.setCode19((String) map.get("code19"));
		sqlSysUser.setCode20((String) map.get("code20"));

		return sqlSysUser;
	}

	//test
	public static void main(String[] args) {
		SqlUtilsService service = new SqlUtilsService();
		service.init();

		service.getEntity();
	}
}
