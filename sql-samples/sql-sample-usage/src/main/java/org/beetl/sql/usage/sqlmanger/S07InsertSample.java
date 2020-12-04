package org.beetl.sql.usage.sqlmanger;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.IDAutoGen;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 使用sqlManager 插入操作
 *
 * @author xiandafu
 */
public class S07InsertSample {
    SQLManager sqlManager;

    public S07InsertSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public static void main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S07InsertSample sample = new S07InsertSample(sqlManager);
        sample.basic();
        sample.idGenerator();
        sample.insertTemplate();
        sample.batchInsert();
        sample.batchInsertByMap();

        sample.insertByResourceId();
        sample.execute();
        sample.insertAutoValue();

    }

    /**
     * 最常用的情况
     */
    public void basic() {
        // UserEntity 的主键是auto
        UserEntity user = new UserEntity();
        user.setName("abc");
        user.setDepartmentId(1);
        sqlManager.insert(user);
        //自动赋值到实体里
        System.out.println(user.getId());

        //使用@AssingId
        sqlManager.deleteById(UserInfo.class, 999);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(999);
        userInfo.setName("bac");
        sqlManager.insert(userInfo);


    }

    /**
     * 使用id生成器，比如生成uuid
     */
    public void idGenerator() {
        //初始化一个id生成器
        sqlManager.addIdAutoGen("myIdGenerator", new IDAutoGen() {

            @Override
            public Object nextID(String params) {
                System.out.println(params);
                Random random = new Random();
                int base = 100000;
                int ret = base + random.nextInt(10000);
                return ret;

            }
        });

        //使用@AssingId
        UserInfo2 userInfo = new UserInfo2();
        userInfo.setName("bac");
        sqlManager.insert(userInfo);
        Integer id = userInfo.getId();
        System.out.println(id);
        sqlManager.deleteById(UserInfo2.class, userInfo.getId());


    }


    /**
     * 有值才插入
     */
    public void insertTemplate() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("lij");
        sqlManager.insertTemplate(userEntity);
        System.out.println(userEntity.getId());
    }

    /**
     * 批量插入
     */
    public void batchInsert() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("lij");
        userEntity.setDepartmentId(1);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setName("ok");
        userEntity.setDepartmentId(1);
        //注意，日志输出只有一条，但更新输出了所有更新结果
        sqlManager.insertBatch(UserEntity.class, Arrays.asList(userEntity, userEntity2));

    }

    public void batchInsertByMap() {
        Map map = new HashMap();
        map.put("name", "ok3");
        map.put("departmentId", 5);
        sqlManager.insert(UserEntity.class, map);

    }

    public void insertByResourceId() {

        SqlId insertId = SqlId.of("insertSample", "insertUser");
        UserEntity entity = new UserEntity();
        entity.setName("lucy");
        entity.setDepartmentId(12);
        ;
        sqlManager.insert(insertId, entity);
        System.out.println(entity.getId());

        Map map = new HashMap();
        map.put("name", "ok3");
        map.put("departmentId", 5);
        sqlManager.insert(insertId, map);


    }

    public void execute() {

        String sql = "insert into sys_user (name,department_id) values (?,?)";
        Object[] args = {"ba", 23};
        sqlManager.executeUpdate(new SQLReady(sql, args));

        String template = "insert into sys_user (name,department_id) values (#{name},#{deptId})";
        //使用map或者pojo作为参数
        Map map = new HashMap();
        map.put("name", "ok3");
        map.put("deptId", 5);
        sqlManager.executeUpdate(template, map);

    }

    /**
     * 插入后获取数据库自动生成的值
     */
    public void insertAutoValue() {

        SqlId insertId = SqlId.of("insertSample", "insertUser");
        Map map = new HashMap();
        map.put("name", "ok3");
        map.put("departmentId", 5);

        //如果需要知道插入后的数据库自动生成的值值(自增主键，数据库自动生成的值等)，可以提供一个列名列表
        Object[] autoValue = sqlManager.insert(insertId, map, new String[]{"id"});
        System.out.println(autoValue[0]);

    }


    @Data
    @Table(name = "sys_user")
    public static class UserInfo {
        @AssignID
        Integer id;
        String name;
    }

    /**
     * 使用id生成器
     */
    @Data
    @Table(name = "sys_user")
    public static class UserInfo2 {
        @AssignID(value = "myIdGenerator", param = "辅助参数")
        Integer id;
        String name;
    }


}
