package org.beetl.sql.test;




import org.beetl.sql.core.DSTransactionManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.DepartmentEntity;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.test.mapper.UserMapper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 入门 演示内置SQLManager用法和BaseMapper用法，项目中更推荐使用BaseMapper，而不是较为底层的SQLManager
 * @author xiandafu
 *
 */

public class S1QuickStart {

    SQLManager sqlManager;
    UserMapper mapper = null;

    public S1QuickStart(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
        mapper = sqlManager.getMapper(UserMapper.class);
    }

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S1QuickStart quickStart = new S1QuickStart(sqlManager);
//        quickStart.baseSqlManager();
//        quickStart.executeSql();
//        quickStart.executeTemplate();
//        quickStart.query();
//        quickStart.mapper();
        quickStart.sqlResource();
//        quickStart.stream();
    }

    /**
     * 使用内置sqlManager方法
     */

    public  void baseSqlManager(){
        UserEntity user  = sqlManager.unique(UserEntity.class,1);

        user.setName("ok123");
        sqlManager.updateById(user);

        UserEntity newUser = new UserEntity();
        newUser.setName("newUser");
        newUser.setDepartmentId(1);
        sqlManager.insert(newUser);


        UserEntity template = new UserEntity();
        template.setDepartmentId(1);
        List<UserEntity> list = sqlManager.template(template);



    }


    //执行sql语句方法
    public  void executeSql(){
        String sql = "select * from sys_user where id=?";
        Integer id  = 1;
        SQLReady sqlReady = new SQLReady(sql,new Object[]{id});
        List<UserEntity> userEntities = sqlManager.execute(sqlReady,UserEntity.class);

        String updateSql = "update department set name=? where id =?";
        String name="lijz";
        SQLReady updateSqlReady = new SQLReady(updateSql,new Object[]{name,id});
        sqlManager.executeUpdate(updateSqlReady);


    }

    //执行sql模板语句
    public  void executeTemplate(){

        {
            String sql = "select * from sys_user where department_id=#{id} and name=#{name}";
            UserEntity paras = new UserEntity();
            paras.setDepartmentId(1);
            paras.setName("lijz");
            List<UserEntity> list = sqlManager.execute(sql,UserEntity.class,paras);
        }

        {
            //或者使用Map作为参数
            String sql = "select * from sys_user where department_id=#{myDeptId} and name=#{myName}";
            Map paras = new HashMap();
            paras.put("myDeptId",1);
            paras.put("myName","lijz");
            List<UserEntity> list = sqlManager.execute(sql,UserEntity.class,paras);
        }

        {
            //使用Beetl模板语句
            String sql = "select * from sys_user where 1=1 \n" +
                        "-- @if(isNotEmpty(myDeptId)){\n" +
                        "   and department_id=#{myDeptId}\t\n" +
                        "-- @}\n" +
                        "and name=#{myName}";


            Map paras = new HashMap();
            paras.put("myDeptId",1);
            paras.put("myName","lijz");
            List<UserEntity> list = sqlManager.execute(sql,UserEntity.class,paras);

        }



    }

    public  void query(){
        {
            Query<UserEntity> query = sqlManager.query(UserEntity.class);
            List<UserEntity> entities = query.andEq("department_id",1)
                    .andIsNotNull("name").select();
        }
        {
            //使用LambdaQuery，能很好的支持数据库重构
            LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
            List<UserEntity> entities = query.andEq(UserEntity::getDepartmentId,1)
                    .andIsNotNull(UserEntity::getName).select();
        }



    }


    /**
     * 最常用的方式，编写一个Mapper类，mapper方法提供数据库访问接口，beetlsql提供丰富的beetlsql实现
     */
    public  void mapper(){


//        内置BaseMapper方法调用
        List<UserEntity> list = mapper.all();
        boolean isExist = mapper.exist(2);
        UserEntity me = mapper.unique(1);
        me.setName("newName");
        mapper.updateById(me);

        //调用其他方法
        UserEntity user = mapper.getUserById(1);
        UserEntity user2 = mapper.queryUserById(2);
        mapper.updateName("newName2",2);

        List<UserEntity> users = mapper.queryByNameOrderById("newName2");

        List<DepartmentEntity> depts = mapper.findAllDepartment();

    }

    /**
     * 对于复杂sql语句，比如几十行，甚至几百行的sql模板语句，放到markdown文件里是个不错的想法
     * 参考sql/user.md#select
     */
    public  void sqlResource(){
        SqlId id = SqlId.of("user","selectUserByName");
        //or SqlId id = SqlId.of("user.select");
        Map map = new HashMap();
        map.put("name","n");
        List<UserEntity> list = sqlManager.select(id,UserEntity.class,map);

        mapper.selectUserByName("n");

    }


	/**
	 * 对于需要处理海量数据，可以使用stream api查询，返回StreaData对象
	 * 参考sql/user.md#select
	 */
	public  void stream() throws SQLException {
		//不同于其他beetlsql api，stream对象包含了数据库链接，因此查询完毕后，脱离了beetlsql管理，因此期待在
		//事物上下文里被调用，这里采用beetlsql内置的事物管理器，spring框架可以使用spring事物管理，而不需要DSTransactionManager
		DSTransactionManager.start();
		SQLReady sqlReady = new SQLReady("select * from sys_user");
		StreamData<UserEntity> streamData = sqlManager.streamExecute(sqlReady,UserEntity.class);
		streamData.foreach(user -> {
			System.out.println(user.getName());
		});

		streamData = mapper.allUserStream(1);
		streamData.foreach(user -> {
			System.out.println(user.getName());
		});

		DSTransactionManager.commit();

	}

}
