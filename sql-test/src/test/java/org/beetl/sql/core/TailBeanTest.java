package org.beetl.sql.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.clazz.kit.BeanKit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 测试TailBean功能
 */
public class TailBeanTest extends BaseTest {
	String sql = "select u.*,d.name department_name from sys_user u left join department d on u.department_id = d.id where u.id=?";
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }

    @Test
    public void testTailBean(){

		UserView2 userView2 = sqlManager.execute(new SQLReady(sql, 1), UserView2.class).get(0);
		System.out.println(userView2.getId()+" :"+userView2.get("departmentName"));
    }


	@Test
	public void testNewTailBean(){

		UserView3 userView3 = sqlManager.execute(new SQLReady(sql, 1), UserView3.class).get(0);
		System.out.println(userView3.getId()+" :"+userView3.get("departmentName"));
		Assert.assertEquals(userView3.getName(),userView3.get("name"));
	}


	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class UserView2 extends TailBean {
		@AutoID
		private Integer id;
		private String name;

	}


	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class UserView3 extends MyNewTailBean {
		@AutoID
		private Integer id;
		private String name;

	}


	public static class  MyNewTailBean extends  TailBean{
		@Override
		public Object get(String key) {
			Object o = super.get(key);
			if(o==null&&BeanKit.getPropertyDescriptorWithNull(this.getClass(),key)!=null){
				return BeanKit.getBeanProperty(this,key);
			}

			return o;

		}
	}



}
