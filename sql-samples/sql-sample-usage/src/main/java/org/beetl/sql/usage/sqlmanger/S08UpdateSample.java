package org.beetl.sql.usage.sqlmanger;

import org.beetl.sql.core.BatchParam;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用sqlManager 更新或者删除
 * @author xiandafu
 */
public class S08UpdateSample {
    SQLManager sqlManager;

    public S08UpdateSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public static void main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S08UpdateSample sample = new S08UpdateSample(sqlManager);
        sample.basicUpdate();
        sample.basicDelete();
        sample.batchUpdate();
        sample.execute();

        sample.resourceId();
        sample.batchUpdateByResourceId();
		sample.batchByDiffSqlTemplate();


    }

    /**
     * 最常用的情况
     */
    public void basicUpdate() {

        //全部更新
        UserEntity user = new UserEntity();
        user.setName("abc");
        user.setDepartmentId(1);
        user.setId(21);
        sqlManager.updateById(user);

        //部分更新，只更新不为null的
        UserEntity user2 = new UserEntity();
        user2.setName("新的名字");
        user2.setId(21);
        sqlManager.updateTemplateById(user2);

        //或者是Map提供参数
        Map map = new HashMap();
        map.put("departmentId",3);
        map.put("id",21);
        sqlManager.updateTemplateById(UserEntity.class,map);


    }

    public void basicDelete(){
        Integer id = 65;
        sqlManager.deleteById(UserEntity.class,id);

        /*仍然是按照id删除*/
        UserEntity user = new UserEntity();
        user.setId(21);
        sqlManager.deleteObject(user);

    }

    public void batchUpdate(){

        UserEntity user = new UserEntity();
        user.setName("abc");
        user.setDepartmentId(1);
        user.setId(21);

        UserEntity user2 = new UserEntity();
        user2.setName("abc");
        user2.setDepartmentId(1);
        user2.setId(11);

        //更新全部
        sqlManager.updateByIdBatch(Arrays.asList(user,user2));

        //更新有值的对象
        sqlManager.updateBatchTemplateById(UserEntity.class,Arrays.asList(user,user2));

    }

    public void execute(){
        String sql = "update sys_user set name=? where id = ?";
        Object[] args = {"abc",23};
        int ret = sqlManager.executeUpdate(new SQLReady(sql,args));

        String template = "update sys_user set name=#{name} where id = #{id}";
        //也可以使用Map作为参数
        UserEntity entity = new UserEntity();
        entity.setId(23);
        entity.setName("abc");
        sqlManager.executeUpdate(template,entity);

        String deleteSql = "delete from  sys_user  where id = ?";
        Object[] deleteArgs = {23};
        ret = sqlManager.executeUpdate(new SQLReady(deleteSql,deleteArgs));


    }

    /**
     * 使用sql文件
     */
    public void resourceId(){
        SqlId updateById = SqlId.of("insertSample", "updateUser");
        Map map = new HashMap();
        map.put("name","abc");
        map.put("id",21);
        sqlManager.update(updateById,map);


    }


    public void batchUpdateByResourceId(){
        /**
         *  批处理,需要注意，批处理更新，虽然是同样的模板sql，但实际上可能是个不同的jdbc sql，因此
         *  beetlsql是分批处理的，日志也是多条，针对不同的jdbc sql
         */

        UserEntity user = new UserEntity();
        user.setName("abc");
        user.setDepartmentId(1);
        user.setId(21);

        UserEntity user2 = new UserEntity();
        user2.setName("abc");
        user2.setDepartmentId(1);
        user2.setId(11);
        SqlId updateById = SqlId.of("insertSample", "updateUser");
        sqlManager.updateBatch(updateById,Arrays.asList(user,user2));

    }

	public void batchByDiffSqlTemplate() {
		/**
		 * 批量执行sql模板，每条sql模板可以有不同参数
		 */
		UserEntity user = new UserEntity();
		user.setName("abc");
		user.setId(1121);

		UserEntity user2 = new UserEntity();
		user2.setName("abcd");
		user2.setDepartmentId(222);
		user2.setId(1111);

		BatchParam param = BatchParam.builder()
			.sqlTemplate("insert into sys_user (id,name) values (#{id},#{name})")
			.sqlParamList(Arrays.asList(user, user2));
		BatchParam param1 = BatchParam.builder()
			.sqlTemplate("update sys_user set \n" +			//注意：使用String类型的sql模板并且使用到了函数，必须采用\n换行，否则报语法错误
				" -- @if(!isEmpty(departmentId)){ \n" +
				"     department_id=#{departmentId}, \n" +
				" -- @}\n " +
				" -- @if(!isEmpty(name)){\n " +
				"     name=#{name}\n " +
				" -- @}\n " +
				" where id=#{id}")
				.sqlParamList(Arrays.asList(user, user2));
		sqlManager.setBatchLogOneByOne(true);
		sqlManager.executeBatch(Arrays.asList(param, param1), null);

		/**
		 *  批量执行sql模板（从md文件读取），每条sql模板可以有不同参数
		 */
		BatchParam param2 = BatchParam.builder()
			.sqlId("user.batchExecSql1")
			.sqlParam(user);
		BatchParam param3 = BatchParam.builder()
			.sqlId("user.batchExecSql2")
			.sqlParam(user2);
		sqlManager.executeBatch(Arrays.asList(param2, param3), null);

	}










}
