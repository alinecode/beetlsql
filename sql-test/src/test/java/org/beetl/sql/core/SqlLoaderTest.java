package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.loader.SQLLoader;
import org.beetl.sql.core.nosql.DruidStyle;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SqlLoaderTest extends BaseTest {

    @BeforeClass
    public static void init() {
        initTable(testSqlFile);
    }



    @Test
    public void loadSqlFromMd() {
        SqlId id =SqlId.of("user","queryById");
        SQLLoader loader = sqlManager.getSqlLoader();
        loader.setDbStyle(new DruidStyle());
        SQLSource sqlSource = loader.querySQL(id);
        Assert.assertNotNull(sqlSource);
        sqlManager.setDbStyle(new DruidStyle());

        SqlId id2 =SqlId.of("user","druidxxxx");
        SQLSource sqlSource2 = loader.querySQL(id2);
        Assert.assertNotNull(sqlSource2);
        Assert.assertTrue(sqlSource2.getTemplate().contains("/* from druid */"));



    }








}
