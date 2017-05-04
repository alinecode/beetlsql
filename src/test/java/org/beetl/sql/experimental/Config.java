package org.beetl.sql.experimental;

import org.beetl.sql.core.SQLManager;

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

//        // 这个类是不提交的,里面做的就是改变了Config的数据库连接信息.
//        PrivateConfig.settingConfig();

        // 使用SQLManager构建器构建 SQLManager, 并添加一个sql打印插件.
        sqlManager = SQLManager.newBuilder(driver, url, userName, password).addInterDebug().build();
    }
}
