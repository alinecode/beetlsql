package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.MethodDesc;
import org.beetl.sql.core.senior.MethodDescBuilder;
import org.beetl.sql.core.senior.SeniorConfig;
import org.beetl.sql.core.senior.SeniorConfigBuilder;
import org.beetl.sql.experimental.Config;
import org.beetl.sql.experimental.domain.BeeBird;
import org.beetl.sql.experimental.methoddesc.MethodDescExperimental;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;

/**
 * <BR>
 * create time : 2017-04-27 18:00
 *
 * @author luoyizhu@gmail.com
 */
public class AmiInnerProxyMapperInvokeTest {

    @Test
    public void testOriginal() {
        // 原始测试
        MapperInvoke invoke = SeniorConfig.$.getInternalAmi("insert");
        Assert.notNull(invoke);

        invoke = SeniorConfig.$.getMapperInvokeProxy(BaseMapper.class);

        Assert.notNull(invoke);

        System.out.println(invoke);
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

        Config.$.dbInit();

        // 这里开始是扩展测试用例
//        SeniorConfigBuilder builder = SeniorConfig.$.getBuilder();

        /*
         * 扩展一个自定义接口不使用 BaseMapper.
         * 松耦合编程. 不强制用户与beetlsql耦合, 站在用户的角度是为了让用户无伤切换到其他orm框架.
         * MyMapper接口使用 AmiInnerProxyMapperInvoke 对象来处理
         * AmiInnerProxyMapperInvoke默认实现了 BaseMapper 的所有实现方法
         */
        SeniorConfigBuilder.addBaseMapper(MyMapper.class);

        /*
          这两个方法名与 MyMapper接口保持一致.
          为了告诉beetlsql, 遇见这个方法名, 帮我用对应的实现类来处理.
          这样扩展性更高, 更自由.不必等着开源作者来提供实现.
          */
        SeniorConfigBuilder.addAmi("selectAll", new AllAmi());
        SeniorConfigBuilder.addAmi("selectCount", new AllCountAmi());

        BeeBird.Dao dao = Config.$.sqlManager.getMapper(BeeBird.Dao.class);

        long count = dao.selectCount();

        System.out.println("count: " + count);

        List<BeeBird> birds = dao.selectAll();

        System.out.println(birds);
    }


    /**
     * 由于这个需要java8 支持,这里不好做测试用例.
     * 详情查看: http://git.oschina.net/iohao/beetlsql-experimental
     * 不过现在上面还没有代码,还未提交. 等出了博客后在提交.
     */
    @Test
    public void testMethodDescBuilder() {
        Config.$.dbInit();
        SeniorConfigBuilder builder = SeniorConfig.$.getBuilder();
        builder.setMethodDescBuilder(new MethodDescBuilder() {
            @Override
            public MethodDesc create() {
                return new MethodDescExperimental();
            }
        });

        builder.build();
    }


}
