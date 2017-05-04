package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.builder.MapperConfig;
import org.beetl.sql.core.mapper.builder.MapperConfigBuilder;
import org.beetl.sql.experimental.Config;
import org.beetl.sql.experimental.domain.BeeBird;
import org.beetl.sql.test.mysql.BaseMySqlTest;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <BR>
 * create time : 2017-04-27 18:00
 *
 * @author luoyizhu@gmail.com,xiandafu
 */
public class AmiInnerProxyMapperInvokeTest  extends BaseMySqlTest {

//    SQLManager sqlManager;
//
//    @Before
//    public void start() {
//        Config.$.dbInit();
//        this.sqlManager = Config.$.sqlManager;
//    }

    @Test
    public void testOriginal() {
        // 原始测试
        MapperConfig mapperConfig = this.sqlManager.getMapperConfig();
        MapperInvoke invoke = mapperConfig.getAmi(BaseMapper.class, "insert");

        Assert.notNull(invoke);

        Assert.isTrue(invoke instanceof InsertAmi);
    }

    /**
     * <pre>
     * 可能下面说得有点乱. 如果看不懂看这里的解释!
     * 为了不必强制用户继承 BaseMapper 接口才能使用内置实现, 而是通过配置映射的方式
     * 在把内置实现代理独立出来的同时, 又提供方法实现的扩展(Ami).
     * 这个方法实现(Ami)除了提供扩展的同时,又不必强制用户使用这个方法名, 让用户更自由
     * 想上面举出了两个示例:
     * 原来内置的方法名是 all , 我改成了 selectAll
     * 原来内置的方法名是 allCount , 我改成了 selectCount
     * </pre>
     */
    @Test
    public void testAmi() {

        // 自定义一个基接口, 并获取基接口配置构建器
        MapperConfigBuilder builder = this.sqlManager.setBaseMapper(MyMapper.class).getBuilder();

        /*
         * 这两个方法名与 MyMapper接口保持一致. 为了告诉beetlsql, 遇见这个方法名, 帮我用对应的实现类来处理. 这样扩展性更高,
         * 更自由.不必等着开源作者来提供实现.
         *
         * 里面已经内置BaseMapper的所有方法, 用户只需要在自定义的基接口上定义与BaseMapper相同的方法名就可以使用
         */
        builder.addAmi("selectCount", new AllCountAmi());

        builder.addAmi("selectAll", new AllAmi());

        UserDao dao = sqlManager.getMapper(UserDao.class);

        long count = dao.selectCount();

        System.out.println("count: " + count);

        List<User> birds = dao.selectAll();

        System.out.println(birds);
    }

//    @Test
    public void testMyAmi() {
        // 自定义一个基接口, 并获取基接口配置构建器
        MapperConfigBuilder builder = this.sqlManager.setBaseMapper(MyMapper.class).getBuilder();

        builder.addAmi("selectCount", new AllCountAmi());
        builder.addAmi("selectAll", new AllAmi());

        BeeBird.Dao dao = this.sqlManager.getMapper(BeeBird.Dao.class);

        long count = dao.selectCount();
        Assert.isTrue(count > 0);

        System.out.println("count: " + count);

        List<BeeBird> birds = dao.selectAll();

        System.out.println(birds);
        Assert.isTrue(birds.size() > 0);

        System.out.println("~~~~");

        // 这里测试beetlsql 的 BaseMapper.
        BeeBird.BaseDao baseDao = this.sqlManager.getMapper(BeeBird.BaseDao.class);

        birds = baseDao.all();

        Assert.isTrue(birds.size() > 0);
        System.out.println(birds);


        System.out.println(baseDao.allCount());

        Assert.isTrue(baseDao.allCount() > 0);


        System.out.println("~~~ 添加一个自定义Ami");
        builder.addAmi("selectIds", new MapperInvoke() {
            @Override
            public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
                String tableName = sm.getNc().getTableName(entityClass);

                TableDesc tableDesc = sm.getMetaDataManager().getTable(tableName);
                StringBuilder builder = new StringBuilder("select id from ").append(tableDesc.getName());
                return sm.execute(new SQLReady(builder.toString()), Integer.class);
            }
        });

        List<Integer> ids = dao.selectIds();
        System.out.println(ids);
        Assert.isTrue(ids.size() > 0);

    }

    /**
     * 由于这个需要java8 支持,这里不好做测试用例. 详情查看:
     * http://git.oschina.net/iohao/beetlsql-experimental 不过现在上面还没有代码,还未提交.
     * 等出了博客后在提交.
     */
    @Test
    public void testMethodDescBuilder() {
        //        Config.$.dbInit();
        //        MapperConfigBuilder builder = MapperConfig.$.getBuilder();
        //        builder.setMethodDescBuilder(new MethodDescBuilder() {
        //            @Override
        //            public MethodDesc create() {
        //                return new MethodDescExperimental();
        //            }
        //        });
        //
        //        builder.build();
    }

}
