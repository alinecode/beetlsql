package org.beetl.sql.starter;

import lombok.Data;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.ConditionalSQLManager;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;


@Data
public class BeetlSqlConfig {
    public static final String PREFIX="beetlsql";
    Environment env = null;
    public BeetlSqlConfig(Environment env){
        this.env = env;
        init();
    }


    Map<String,SQLManagerConfig> configs= new HashMap<>();

    public void init() {
        SQLManagerConfig defaultConfig = SQLManagerConfig.initDefault(env);
		String[] allSqlManangerNames = null;
		String oldSqlManagerConfig = env.getProperty("beetlsql");
		if(!StringKit.isBlank(oldSqlManagerConfig)){
			allSqlManangerNames = oldSqlManagerConfig.split(",");
		}else{
			if(StringKit.isBlank(env.getProperty("beetlsql.sqlManagers"))){
				throw new IllegalArgumentException("缺少 beetlsql.sqlManagers 配置");
			}
			allSqlManangerNames = env.getProperty("beetlsql.sqlManagers").split(",");
		}
        for(String s:allSqlManangerNames){
            SQLManagerConfig sqlManagerConfig = new SQLManagerConfig(env,s,defaultConfig);
            configs.put(s,sqlManagerConfig);
        }

    }

    @Data
    public static class SQLManagerConfig{
        String basePackage = null;// 配置beetlsql.daoSuffix来自动扫描com包极其子包下的所有以Dao结尾的Mapper类
        String daoSuffix = null;// 通过类后缀 来自动注入Dao
        String sqlPath = null;// 存放sql文件的根目录
        String nameConversion = null;// 数据库和javapojo的映射关系
        String dbStyle = null; // 何种数据库
        boolean dev = false;
        String ds = null;

        String dynamicSqlManager;
        String dynamicCondition;



        public SQLManagerConfig(){}

        public static SQLManagerConfig initDefault(Environment env){

            String prefix =PREFIX+"._default";
            String basePackage = env.getProperty(prefix+".basePackage", "com");
            String daoSuffix = env.getProperty(prefix+".daoSuffix", "Mapper");
            String sqlPath = env.getProperty(prefix+".sqlPath", "sql");
            String nameConversion = env.getProperty(prefix+".nameConversion", "org.beetl.sql.core.UnderlinedNameConversion");
            String dbStyle = env.getProperty(prefix+".dbStyle", "org.beetl.sql.core.db.MySqlStyle");
            boolean dev = env.getProperty(prefix+".dev", Boolean.class, true);
            SQLManagerConfig defaultConfig = new  SQLManagerConfig();
            defaultConfig.setBasePackage(basePackage);
            defaultConfig.setDaoSuffix(daoSuffix);
            defaultConfig.setSqlPath(sqlPath);
            defaultConfig.setNameConversion(nameConversion);
            defaultConfig.setDbStyle(dbStyle);
            defaultConfig.setDev(dev);


            return defaultConfig;

        }


        public SQLManagerConfig(Environment env,String sqlManagerName,SQLManagerConfig defaultConfig){
            String prefix =PREFIX+"."+sqlManagerName;
            dynamicSqlManager = env.getProperty(prefix+".dynamic");
            if(!StringKit.isEmpty(dynamicSqlManager)){
                dynamicCondition = env.getProperty(prefix+".dynamic.condition");
                if(StringKit.isEmpty(dynamicCondition)){
                    //默认
                    dynamicCondition = ConditionalSQLManager.DefaultConditional.class.getName();
                }
                basePackage = env.getProperty(prefix+".basePackage", defaultConfig.getBasePackage());
                daoSuffix = env.getProperty(prefix+".daoSuffix", defaultConfig.getDaoSuffix());
                dev = env.getProperty(prefix+".dev", Boolean.class,defaultConfig.isDev());
                return ;
            }
            basePackage = env.getProperty(prefix+".basePackage", defaultConfig.getBasePackage());
            daoSuffix = env.getProperty(prefix+".daoSuffix", defaultConfig.getDaoSuffix());
            sqlPath = env.getProperty(prefix+".sqlPath", defaultConfig.getSqlPath());
            nameConversion = env.getProperty(prefix+".nameConversion", defaultConfig.getNameConversion());
            dbStyle = env.getProperty(prefix+".dbStyle", defaultConfig.getDbStyle());
            dev = env.getProperty(prefix+".dev", Boolean.class,defaultConfig.isDev());
            ds = env.getProperty(prefix+".ds");
            if(ds==null){
                throw new NullPointerException(prefix+".ds 不能为空");
            }
        }
    }
}
