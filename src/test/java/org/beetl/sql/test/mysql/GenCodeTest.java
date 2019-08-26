package org.beetl.sql.test.mysql;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

public class GenCodeTest {

    public static void main(String[] args) throws Exception {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://cyying.com:3306/icemall?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        String userName = "root";
        String password = "8975789757";
        ConnectionSource source = ConnectionSourceHelper.getSimple(driver, url, userName, password);
        DBStyle mysql = new MySqlStyle();
        // sql语句放在classpagth的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/sql");
        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        SQLManager sqlManager = new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});

//        sqlManager.genPojoCodeToConsole("icemall_ad");
//        sqlManager.genSQLTemplateToConsole("icemall_ad");
        String pkg = "com.lol.ccc.icemall.db.domain";

        sqlManager.genPojoCode("icemall_ad",pkg);
        sqlManager.genPojoCode("icemall_address",pkg);
        sqlManager.genPojoCode("icemall_admin",pkg);
        sqlManager.genPojoCode("icemall_brand",pkg);
        sqlManager.genPojoCode("icemall_cart",pkg);
        sqlManager.genPojoCode("icemall_category",pkg);
        sqlManager.genPojoCode("icemall_collect",pkg);
        sqlManager.genPojoCode("icemall_comment",pkg);
        sqlManager.genPojoCode("icemall_feedback",pkg);
        sqlManager.genPojoCode("icemall_footprint",pkg);
        sqlManager.genPojoCode("icemall_goods",pkg);
        sqlManager.genPojoCode("icemall_goods_attribute",pkg);
        sqlManager.genPojoCode("icemall_goods_specification",pkg);
        sqlManager.genPojoCode("icemall_groupon",pkg);
        sqlManager.genPojoCode("icemall_groupon_rules",pkg);
        sqlManager.genPojoCode("icemall_issue",pkg);
        sqlManager.genPojoCode("icemall_keyword",pkg);
        sqlManager.genPojoCode("icemall_order",pkg);
        sqlManager.genPojoCode("icemall_order_goods",pkg);
        sqlManager.genPojoCode("icemall_product",pkg);
        sqlManager.genPojoCode("icemall_region",pkg);
        sqlManager.genPojoCode("icemall_search_history",pkg);
        sqlManager.genPojoCode("icemall_storage",pkg);
        sqlManager.genPojoCode("icemall_system",pkg);
        sqlManager.genPojoCode("icemall_topic",pkg);
        sqlManager.genPojoCode("icemall_user",pkg);
        sqlManager.genPojoCode("icemall_user_formid",pkg);
    }
}
