package org.beetl.sql.usage.query;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;

/**
 * 很少用Query来实现删除，修改和新增，建议使用BaseMapper或者SQLManager的内置方法，带条件复杂的更新操作也最好用sql来实现
 * 演示如何使用query进行修改和删除
 * @author xiandafu
 */
public class S05QueryUpdateSample {

    SQLManager sqlManager;

    public S05QueryUpdateSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public static void main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S05QueryUpdateSample sample = new S05QueryUpdateSample(sqlManager);
//        sample.delete();
        sample.update();
//        sample.insert();



    }

    public void delete(){
//        //删除符合条件的数据
        Query<UserEntity> deleteQuery = sqlManager.query(UserEntity.class);
        int count = deleteQuery.andEq("name","ok")
                .andEq("department_id",99).delete();
        //或者使用LambdaQuery，数据库重构时候很容易调整java代码
        LambdaQuery<UserEntity> deleteQuery2 = sqlManager.lambdaQuery(UserEntity.class);
        count = deleteQuery2.andEq(UserEntity::getName,"ok")
                .andEq(UserEntity::getDepartmentId,99).delete();



    }

    /**
     * 更改
     */
    public void update(){

        //更新user中提供的值，如下把所有部门id为21的更新为24
        UserEntity user = new UserEntity();
        user.setDepartmentId(24);
        LambdaQuery<UserEntity> updateQuery = sqlManager.lambdaQuery(UserEntity.class);
        updateQuery.andEq(UserEntity::getDepartmentId,21).updateSelective(user);

        //更新所有值(主键不在更新范围内），一下更新部门id和人名
        UserEntity user2 = new UserEntity();
        user2.setDepartmentId(24);
        user2.setName("新的名字");
        LambdaQuery<UserEntity> updateQuery2 = sqlManager.lambdaQuery(UserEntity.class);
        updateQuery2.andEq(UserEntity::getId,5).update(user2);


    }

    /**
     * 插入操作
     */
    public void insert(){

        /*同sqlManger.insert()*/
        UserEntity user = new UserEntity();
        user.setDepartmentId(24);
        user.setName("新的名字");
        LambdaQuery<UserEntity> insertQuery = sqlManager.lambdaQuery(UserEntity.class);
        insertQuery.insert(user);
        /*仅仅插入值不为空的属性，同sqlManager.insertTemplate()*/
        user.setDepartmentId(null);
        insertQuery.insertSelective(user);



    }



}
