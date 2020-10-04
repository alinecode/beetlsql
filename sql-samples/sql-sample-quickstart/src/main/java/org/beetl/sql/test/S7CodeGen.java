package org.beetl.sql.test;


import lombok.Data;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ReThrowConsoleErrorHandler;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.fetech.annotation.Fetch;
import org.beetl.sql.fetech.annotation.FetchMany;
import org.beetl.sql.fetech.annotation.FetchOne;
import org.beetl.sql.gen.SourceBuilder;
import org.beetl.sql.gen.SourceConfig;
import org.beetl.sql.gen.simple.*;
import org.beetl.sql.sample.SampleHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 演示beetlsql 代码生成框架
 *
 * @author xiandafu
 */

public class S7CodeGen {

    SQLManager sqlManager;


    public S7CodeGen(SQLManager  sqlManager) {
       this.sqlManager = sqlManager;
        initGroupTemplate();
    }

    protected  void initGroupTemplate(){
        //指定模板文件路径，正常情况下，不需要要指定，默认在classpath:templates，但idea的环境读取不到
        GroupTemplate groupTemplate = BaseTemplateSourceBuilder.getGroupTemplate();
        String root = System.getProperty("user.dir");
        //代码模板在sql-gen，你可以指定自己的模板路径
        String templatePath = root+"/sql-gen/src/main/resources/templates/";
        FileResourceLoader  resourceLoader = new FileResourceLoader(templatePath);
        groupTemplate.setResourceLoader(resourceLoader);
    }

    public static void main(String[] args) throws Exception {
        //为了简单起见，俩个sqlManager都来自同一个数据源，实际是不同数据库，甚至是NOSQL
        SQLManager sqlManager = SampleHelper.init();
        S7CodeGen gen = new S7CodeGen(sqlManager);
        gen.genCode();
        gen.genDoc();
        gen.genAllDoc();
    }

    /**
     * 代码生成，生成实体，mapper代码
     */
    public void genCode(){
        List<SourceBuilder> sourceBuilder = new ArrayList<>();
        SourceBuilder entityBuilder = new EntitySourceBuilder();
        SourceBuilder mapperBuilder = new MapperSourceBuilder();
        SourceBuilder mdBuilder = new MDSourceBuilder();

		sourceBuilder.add(entityBuilder);
		sourceBuilder.add(mapperBuilder);
		sourceBuilder.add(mdBuilder);

        SourceConfig config = new SourceConfig(sqlManager,sourceBuilder);
        //如果有错误，抛出异常而不是继续运行1
        EntitySourceBuilder.getGroupTemplate().setErrorHandler(new ReThrowConsoleErrorHandler() );

        ConsoleOnlyProject project = new ConsoleOnlyProject();
        String tableName = "sys_user";
        config.gen(tableName,project);
    }


    /**
     * 生成数据库文档
     */
    public void genDoc(){
        List<SourceBuilder> sourceBuilder = new ArrayList<>();
        SourceBuilder docBuilder = new MDDocBuilder();

        sourceBuilder.add(docBuilder);

        SourceConfig config = new SourceConfig(sqlManager,sourceBuilder);
        //如果有错误，抛出异常而不是继续运行1
        EntitySourceBuilder.getGroupTemplate().setErrorHandler(new ReThrowConsoleErrorHandler() );

        ConsoleOnlyProject project = new ConsoleOnlyProject();
        String tableName = "sys_user";
        config.gen(tableName,project);
    }


    /**
     * 生成数据库文档
     */
    public void genAllDoc(){
        List<SourceBuilder> sourceBuilder = new ArrayList<>();
        SourceBuilder docBuilder = new MDDocBuilder();
        sourceBuilder.add(docBuilder);

        SourceConfig config = new SourceConfig(sqlManager,sourceBuilder);
        //如果有错误，抛出异常而不是继续运行1
        EntitySourceBuilder.getGroupTemplate().setErrorHandler(new ReThrowConsoleErrorHandler() );

        StringOnlyProject project = new StringOnlyProject();
        config.genAll(project);
        String output = project.getContent();
        System.out.println(output);
    }


}
