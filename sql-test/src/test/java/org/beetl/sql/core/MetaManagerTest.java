package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.clazz.ColDesc;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.meta.SchemaMetadataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * 测试metaManager
 */
public class MetaManagerTest extends BaseTest {


    @Before
    public void init(){
        super.initTable("/db/db-init.sql");
    }
      @Test
    public void metadata(){

        ConnectionSource cs = ConnectionSourceHelper.getSingle(dataSource);
        DBStyle style = new MySqlStyle();
        SchemaMetadataManager metadataManager = new SchemaMetadataManager(cs,style);
        TableDesc desc = metadataManager.getTable("sys_user");
        TableDesc desc2 = metadataManager.getTable("SYS_USER");
        Assert.assertNotNull(desc);
        Assert.assertNotNull(desc2);
        Set cols = desc.getCols();
        Assert.assertTrue(cols.contains("department_id"));
        Assert.assertTrue(cols.contains("id"));
        Set ids = desc.getIdNames();
        Assert.assertTrue(ids.size()==1&&ids.contains("id"));
        ColDesc colDesc = desc.getColDesc("ID");
        Assert.assertTrue(colDesc.isAuto());
    }
}
