package org.beetl.sql.jmh.sqltutils;

import org.beetl.sql.jmh.base.BaseService;
import org.beetl.sql.jmh.base.DataSourceHelper;
import org.beetl.sql.jmh.sqltutils.model.SQLSysUser;
import org.noear.solon.data.sql.SqlUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			db.sql("insert into sys_user  (id,code,code1,code2,code3,code4,code5,code6,code7,code8,code9,code10,code11,code12,code13,code14,code15,code16,code17,code18,code19,code20) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
				.update(sqlSysUser, this::bindTo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public Object getEntity() {
		try {
			SQLSysUser entity = db.sql("select * from sys_user where id=?", 1)
				.queryRow(this::bindFrom);

			return entity;
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
		getEntity();
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

		try {
			List<SQLSysUser> list = db.sql("select * from sys_user")
				.queryRowList(this::bindFrom);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void bindTo(PreparedStatement ps, SQLSysUser sqlSysUser) throws SQLException {
		ps.setInt(1, sqlSysUser.getId());
		ps.setString(2, sqlSysUser.getCode());
		ps.setString(3, sqlSysUser.getCode1());
		ps.setString(4, sqlSysUser.getCode2());
		ps.setString(5, sqlSysUser.getCode3());
		ps.setString(6, sqlSysUser.getCode4());
		ps.setString(7, sqlSysUser.getCode5());
		ps.setString(8, sqlSysUser.getCode6());
		ps.setString(9, sqlSysUser.getCode7());
		ps.setString(10, sqlSysUser.getCode8());
		ps.setString(11, sqlSysUser.getCode9());
		ps.setString(12, sqlSysUser.getCode10());
		ps.setString(13, sqlSysUser.getCode11());
		ps.setString(14, sqlSysUser.getCode12());
		ps.setString(15, sqlSysUser.getCode13());
		ps.setString(16, sqlSysUser.getCode14());
		ps.setString(17, sqlSysUser.getCode15());
		ps.setString(18, sqlSysUser.getCode16());
		ps.setString(19, sqlSysUser.getCode17());
		ps.setString(20, sqlSysUser.getCode18());
		ps.setString(21, sqlSysUser.getCode19());
		ps.setString(22, sqlSysUser.getCode20());
	}

	SQLSysUser bindFrom(ResultSet rs) throws SQLException {
		SQLSysUser sqlSysUser = new SQLSysUser();

		sqlSysUser.setId(rs.getInt(1));
		sqlSysUser.setCode(rs.getString(2));
		sqlSysUser.setCode1(rs.getString(3));
		sqlSysUser.setCode2(rs.getString(4));
		sqlSysUser.setCode3(rs.getString(5));
		sqlSysUser.setCode4(rs.getString(6));
		sqlSysUser.setCode5(rs.getString(7));
		sqlSysUser.setCode6(rs.getString(8));
		sqlSysUser.setCode7(rs.getString(9));
		sqlSysUser.setCode8(rs.getString(10));
		sqlSysUser.setCode9(rs.getString(11));
		sqlSysUser.setCode10(rs.getString(12));
		sqlSysUser.setCode11(rs.getString(13));
		sqlSysUser.setCode12(rs.getString(14));
		sqlSysUser.setCode13(rs.getString(15));
		sqlSysUser.setCode14(rs.getString(16));
		sqlSysUser.setCode15(rs.getString(17));
		sqlSysUser.setCode16(rs.getString(18));
		sqlSysUser.setCode17(rs.getString(19));
		sqlSysUser.setCode18(rs.getString(20));
		sqlSysUser.setCode19(rs.getString(21));
		sqlSysUser.setCode20(rs.getString(22));

		return sqlSysUser;
	}

	//test
	public static void main(String[] args) {
		SqlUtilsService service = new SqlUtilsService();
		service.init();

		service.getEntity();
	}
}
