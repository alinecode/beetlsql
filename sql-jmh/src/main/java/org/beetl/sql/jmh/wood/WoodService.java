package org.beetl.sql.jmh.wood;

import org.beetl.sql.jmh.BaseService;
import org.beetl.sql.jmh.DataSourceHelper;
import org.beetl.sql.jmh.wood.mapper.WoodSQLUserMapper;
import org.beetl.sql.jmh.wood.model.WoodSQLSysUser;
import org.beetl.sql.jmh.wood.model.WoodSysCustomer;
import org.noear.wood.BaseMapper;
import org.noear.wood.DbContext;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WoodService implements BaseService {
    WoodSQLUserMapper userMapper;
    BaseMapper<WoodSysCustomer> customerMapper;
    AtomicInteger idGen = new AtomicInteger(1000);

    DbContext db;

    public void init() {
        DataSource dataSource = DataSourceHelper.newDatasource();

        this.db = new DbContext("user", dataSource);
        this.userMapper = db.mapper(WoodSQLUserMapper.class);
        this.customerMapper = db.mapperBase(WoodSysCustomer.class);
    }


    @Override
    public void addEntity() {
        WoodSQLSysUser sqlSysUser = new WoodSQLSysUser();
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
        userMapper.insert(sqlSysUser, false);
    }


    @Override
    public Object getEntity() {
        return userMapper.selectById(1);
    }



    @Override
    public void lambdaQuery() {
        List<WoodSQLSysUser> list = userMapper.selectList(wq -> wq.whereEq(WoodSQLSysUser::getId, 1));
    }

    @Override
    public void executeJdbcSql() {
        WoodSQLSysUser user = userMapper.selectById2(1);
    }

    @Override
    public void executeTemplateSql() {
        WoodSQLSysUser user = userMapper.selectTemplateById(1);
    }

    @Override
    public void sqlFile() {
        WoodSQLSysUser user = userMapper.userSelect(1);
    }

    @Override
    public void one2Many() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void pageQuery() {
        List<WoodSQLSysUser> list = userMapper.queryPage("用户一", 1, 5);
        long count = userMapper.selectCount(wq->wq.whereEq("code","用户一"));
    }

    @Override
    public void complexMapping() {
        throw new UnsupportedOperationException();
    }

	@Override
	public void getAll() {
		List<WoodSQLSysUser> woodSQLSysUsers = userMapper.selectList(o -> {
		});
	}

	//
    //模式2
    //
    public void addEntity2() throws SQLException{
        WoodSQLSysUser sqlSysUser = new WoodSQLSysUser();
        sqlSysUser.setId(idGen.getAndIncrement());
        sqlSysUser.setCode("abc");

        db.table("sys_user").setEntity(sqlSysUser).insert();
    }

    public Object getEntity2() throws SQLException{
        return db.table("sys_user")
                .whereEq("id",1)
                .select("*")
                .getList(WoodSQLSysUser.class);
    }

    public void pageQuery2() throws SQLException {
        List<WoodSQLSysUser> list = db.table("sys_user")
                .whereEq("code", "用户一")
                .limit(1, 5)
                .select("*").getList(WoodSQLSysUser.class);
    }
}
