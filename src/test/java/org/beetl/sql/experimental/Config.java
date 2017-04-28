package org.beetl.sql.experimental;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * <pre>
 * 表结构请到这里下载:  http://git.oschina.net/iohao/beetlsql-experimental
 * dir/sql/bird.sql
 * </pre>
 * create time : 2017-04-27 18:02
 *
 * @author luoyizhu@gmail.com
 */
public class Config {
    public static final Config $ = new Config();
    public String userName = "root";
    public String password = "";
    public String url = "jdbc:mysql://localhost:3306/beetlsql?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    public String driver = "com.mysql.jdbc.Driver";
    public SQLManager sqlManager;
    boolean init;

    public void dbInit() {
        if (init) {
            return;
        }

        init = true;

        // 这个类是不提交的,里面做的就是改变了Config的数据库连接信息.
//        PrivateConfig.settingConfig();

        ConnectionSource source = ConnectionSourceHelper.getSimple(driver, url, userName, password);

        SQLManagerBuilder builder = SQLManager.newBuilder(source);

        sqlManager = builder.addInterDebug().build();

//        DBStyle mysql = new MySqlStyle();
//        // sql语句放在classpagth的/sql 目录下
//        SQLLoader loader = new ClasspathLoader("/sql");
//        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
//        NameConversion nc = new DefaultNameConversion();
//        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
//        Interceptor[] inters = new Interceptor[]{new DebugInterceptor()};
//
//        sqlManager = new SQLManager(mysql, loader, source, nc, inters);


    }
}
