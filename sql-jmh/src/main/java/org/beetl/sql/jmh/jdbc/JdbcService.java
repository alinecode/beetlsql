package org.beetl.sql.jmh.jdbc;

import org.beetl.sql.jmh.BaseService;
import org.beetl.sql.jmh.DataSourceHelper;
import org.beetl.sql.jmh.beetl.vo.BeetlSQLSysUser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcService implements BaseService {
	DataSource dataSource = null;
	AtomicInteger idGen = new AtomicInteger(1000);

	public void init() {
		dataSource = DataSourceHelper.ins();
	}


	@Override
	public void addEntity() {

		BeetlSQLSysUser beetlSQLSysUser = new BeetlSQLSysUser();
		beetlSQLSysUser.setId(idGen.getAndIncrement());
		beetlSQLSysUser.setCode("abc");
		beetlSQLSysUser.setCode1("abc");
		beetlSQLSysUser.setCode2("abc");
		beetlSQLSysUser.setCode3("abc");
		beetlSQLSysUser.setCode4("abc");
		beetlSQLSysUser.setCode5("abc");
		beetlSQLSysUser.setCode6("abc");
		beetlSQLSysUser.setCode7("abc");
		beetlSQLSysUser.setCode8("abc");
		beetlSQLSysUser.setCode9("abc");
		beetlSQLSysUser.setCode10("abc");
		beetlSQLSysUser.setCode11("abc");
		beetlSQLSysUser.setCode12("abc");
		beetlSQLSysUser.setCode13("abc");
		beetlSQLSysUser.setCode14("abc");
		beetlSQLSysUser.setCode15("abc");
		beetlSQLSysUser.setCode16("abc");
		beetlSQLSysUser.setCode17("abc");
		beetlSQLSysUser.setCode18("abc");
		beetlSQLSysUser.setCode19("abc");
		beetlSQLSysUser.setCode20("abc");
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement("insert into sys_user  (id,code,code1,code2,code3,code4,code5,code6,code7,code8,code9,code10,code11,code12,code13,code14,code15,code16,code17,code18,code19,code20) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setInt(1, beetlSQLSysUser.getId());
			ps.setString(2, beetlSQLSysUser.getCode());
			ps.setString(3, beetlSQLSysUser.getCode1());
			ps.setString(4, beetlSQLSysUser.getCode2());
			ps.setString(5, beetlSQLSysUser.getCode3());
			ps.setString(6, beetlSQLSysUser.getCode4());
			ps.setString(7, beetlSQLSysUser.getCode5());
			ps.setString(8, beetlSQLSysUser.getCode6());
			ps.setString(9, beetlSQLSysUser.getCode7());
			ps.setString(10, beetlSQLSysUser.getCode8());
			ps.setString(11, beetlSQLSysUser.getCode9());
			ps.setString(12, beetlSQLSysUser.getCode10());
			ps.setString(13, beetlSQLSysUser.getCode11());
			ps.setString(14, beetlSQLSysUser.getCode12());
			ps.setString(15, beetlSQLSysUser.getCode13());
			ps.setString(16, beetlSQLSysUser.getCode14());
			ps.setString(17, beetlSQLSysUser.getCode15());
			ps.setString(18, beetlSQLSysUser.getCode16());
			ps.setString(19, beetlSQLSysUser.getCode17());
			ps.setString(20, beetlSQLSysUser.getCode18());
			ps.setString(21, beetlSQLSysUser.getCode19());
			ps.setString(22, beetlSQLSysUser.getCode20());
			ps.executeUpdate();
			ps.close();
			conn.commit();
			conn.close();

		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Object getEntity() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement("select * from sys_user where id=?");
			ps.setInt(1, 1);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int id = rs.getInt(1);
				String code = rs.getString(2);
				String code1 = rs.getString(3);
				String code2 = rs.getString(4);
				String code3 = rs.getString(5);
				String code4 = rs.getString(6);
				String code5 = rs.getString(7);
				String code6 = rs.getString(8);
				String code7 = rs.getString(9);
				String code8 = rs.getString(10);
				String code9 = rs.getString(11);
				String code10 = rs.getString(12);
				String code11 = rs.getString(13);
				String code12 = rs.getString(14);
				String code13 = rs.getString(15);
				String code14 = rs.getString(16);
				String code15 = rs.getString(17);
				String code16 = rs.getString(18);
				String code17 = rs.getString(19);
				String code18 = rs.getString(20);
				String code19 = rs.getString(21);
				String code20 = rs.getString(22);
				BeetlSQLSysUser beetlSQLSysUser = new BeetlSQLSysUser();
				beetlSQLSysUser.setId(id);
				beetlSQLSysUser.setCode(code);
				beetlSQLSysUser.setCode1(code1);
				beetlSQLSysUser.setCode2(code2);
				beetlSQLSysUser.setCode3(code3);
				beetlSQLSysUser.setCode4(code4);
				beetlSQLSysUser.setCode5(code5);
				beetlSQLSysUser.setCode6(code6);
				beetlSQLSysUser.setCode7(code7);
				beetlSQLSysUser.setCode8(code8);
				beetlSQLSysUser.setCode9(code9);
				beetlSQLSysUser.setCode10(code10);
				beetlSQLSysUser.setCode11(code11);
				beetlSQLSysUser.setCode12(code12);
				beetlSQLSysUser.setCode13(code13);
				beetlSQLSysUser.setCode14(code14);
				beetlSQLSysUser.setCode15(code15);
				beetlSQLSysUser.setCode16(code16);
				beetlSQLSysUser.setCode17(code17);
				beetlSQLSysUser.setCode18(code18);
				beetlSQLSysUser.setCode19(code19);
				beetlSQLSysUser.setCode20(code20);
				return beetlSQLSysUser;
			}
			return null;


		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
				}
			}
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

		Connection conn = null;
		ArrayList<BeetlSQLSysUser> beetlSQLSysUsers = new ArrayList<>();
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement("select * from sys_user");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String code = rs.getString(2);
				String code1 = rs.getString(3);
				String code2 = rs.getString(4);
				String code3 = rs.getString(5);
				String code4 = rs.getString(6);
				String code5 = rs.getString(7);
				String code6 = rs.getString(8);
				String code7 = rs.getString(9);
				String code8 = rs.getString(10);
				String code9 = rs.getString(11);
				String code10 = rs.getString(12);
				String code11 = rs.getString(13);
				String code12 = rs.getString(14);
				String code13 = rs.getString(15);
				String code14 = rs.getString(16);
				String code15 = rs.getString(17);
				String code16 = rs.getString(18);
				String code17 = rs.getString(19);
				String code18 = rs.getString(20);
				String code19 = rs.getString(21);
				String code20 = rs.getString(22);
				BeetlSQLSysUser beetlSQLSysUser = new BeetlSQLSysUser();
				beetlSQLSysUser.setId(id);
				beetlSQLSysUser.setCode(code);
				beetlSQLSysUser.setCode1(code1);
				beetlSQLSysUser.setCode2(code2);
				beetlSQLSysUser.setCode3(code3);
				beetlSQLSysUser.setCode4(code4);
				beetlSQLSysUser.setCode5(code5);
				beetlSQLSysUser.setCode6(code6);
				beetlSQLSysUser.setCode7(code7);
				beetlSQLSysUser.setCode8(code8);
				beetlSQLSysUser.setCode9(code9);
				beetlSQLSysUser.setCode10(code10);
				beetlSQLSysUser.setCode11(code11);
				beetlSQLSysUser.setCode12(code12);
				beetlSQLSysUser.setCode13(code13);
				beetlSQLSysUser.setCode14(code14);
				beetlSQLSysUser.setCode15(code15);
				beetlSQLSysUser.setCode16(code16);
				beetlSQLSysUser.setCode17(code17);
				beetlSQLSysUser.setCode18(code18);
				beetlSQLSysUser.setCode19(code19);
				beetlSQLSysUser.setCode20(code20);
				beetlSQLSysUsers.add(beetlSQLSysUser);
			}


		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
				}
			}
		}
	}
}
