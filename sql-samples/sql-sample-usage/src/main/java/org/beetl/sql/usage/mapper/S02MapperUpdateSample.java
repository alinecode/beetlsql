package org.beetl.sql.usage.mapper;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.usage.mapper.dao.UserUpdateMapper;

import java.util.Arrays;
import java.util.List;

/**
 * 新增，修改和删除都是update
 */
public class S02MapperUpdateSample {
    UserUpdateMapper mapper = null;
    public S02MapperUpdateSample(UserUpdateMapper mapper){
        this.mapper = mapper;
    }
    public static void  main(String[] args){
        SQLManager sqlManager = SampleHelper.getSqlManager();
        UserUpdateMapper mapper = sqlManager.getMapper(UserUpdateMapper.class);
        S02MapperUpdateSample sample = new S02MapperUpdateSample(mapper);
        sample.insert();
        sample.insertTemplate();
        sample.insertBatch();
        sample.updateById();
        sample.updateTemplateById();
        sample.upsert();
        sample.executeUpdate();
        sample.deleteById();
        sample.delete();
        sample.updateBatch();
        sample.updateBySqlId();






    }

    /**
     * 插入
     */
    public void insert(){
        UserEntity entity = new UserEntity();
        entity.setName("ok1");
        entity.setDepartmentId(1);
        //对于@Auto主键，将自动获取主键，@SeqId也是，如果想自己指定主键，则需要使用AssignId
        mapper.insert(entity);
        System.out.println(entity.getId());
    }

    /**
     * 模板插入，为空的属性并没有在sql语句中
     */
    public void insertTemplate(){
        UserEntity entity = new UserEntity();
        entity.setName("ok1");
        mapper.insertTemplate(entity);
        System.out.println(entity.getId());
    }

    /**
     * 批量插入
     */
    public void insertBatch(){
        UserEntity entity1 = new UserEntity();
        entity1.setName("ok1");
        entity1.setDepartmentId(1);

        UserEntity entity2 = new UserEntity();
        entity2.setName("ok2");
        entity2.setDepartmentId(2);
        /**
         * 注意，批处理在打印参数的时候，只打印第一个，并未打印所有参数
         */
        List<UserEntity> data = Arrays.asList(entity1,entity2);
        mapper.insertBatch(data);

    }

    /**
     * 所有属性被更新
     */
    public void updateById(){
        UserEntity entity = new UserEntity();
        entity.setId(1);
        entity.setName("一拳");
        entity.setDepartmentId(1);
        mapper.updateById(entity);
    }

    /**
     * 为空属性不参与更新
     */
    public void updateTemplateById(){
        UserEntity entity = new UserEntity();
        entity.setId(1);
        entity.setName("一拳");
        mapper.updateTemplateById(entity);
    }

    /**
     * 更新或者插入
     */
    public void upsert(){
        UserEntity entity = new UserEntity();
        entity.setId(1);
        entity.setName("abc");
        entity.setDepartmentId(1);
        //存在，所以会更新
        System.out.println(mapper.upsert(entity));
        //不存在，所以会插入
        entity.setId(99999);
        System.out.println(mapper.upsert(entity));
        mapper.deleteById(99999);
    }

    public void executeUpdate(){
        mapper.executeUpdate("update sys_user set name=? where id = ?","name1",1);
        //或者更好的是定义一个接口方法
        mapper.updateName("name1",1);
        //使用模板sql修改
        mapper.updateNameBySqlTemplate("name1",1);

    }

    public void deleteById(){
        UserEntity entity = new UserEntity();
        entity.setName("ok1");
        entity.setDepartmentId(1);
        mapper.insert(entity);
        //通过主键删除
        Integer id = entity.getId();
        mapper.deleteById(id);
    }

    public void delete(){
        mapper.deleteUser("张三");
        mapper.deleteAllUser(Arrays.asList(1001,10001));
    }

    public void updateBatch(){
        UserEntity entity = new UserEntity();
        entity.setId(1);
        entity.setName("ok1");
        entity.setDepartmentId(1);

        UserEntity entity2 = new UserEntity();
        entity2.setId(2);
        entity2.setName("ok1");
        entity2.setDepartmentId(1);

        List list = Arrays.asList(entity,entity2);

        /**
         *  BaseMapper 没有直接支持batch操作，可以调用sqlManger来实现
         *  或者你可以自定义mapper方法，参考BaseMapper和注解AutoMapper
         */

        mapper.batchUpdateById(list);

    }

    public void updateBySqlId(){
        mapper.updateBySqlId("abcd",1);

    }

}
