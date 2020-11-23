package org.beetl.sql.core;

import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.EnumMapping;
import org.beetl.sql.annotation.entity.EnumValue;
import org.beetl.sql.annotation.entity.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 枚举验证
 */
public class EnumSelectTest extends BaseTest {
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }

    @Test
    public void generalEnum(){
    	UserData data = new UserData();
    	data.setName(Name.Li);
        sqlManager.insert(data);
		UserData dbData = sqlManager.unique(UserData.class,data.getId());
		Assert.assertTrue(Name.Li==dbData.getName());

    }

	@Test
	public void testEnumValue(){
		UserData2 data = new UserData2();
		data.setName(Name2.Li);
		sqlManager.insert(data);
		UserData2 dbData = sqlManager.unique(UserData2.class,data.getId());
		Assert.assertTrue(Name2.Li==dbData.getName());

	}


	@Test
	public void testEnumMapping(){
		UserData3 data = new UserData3();
		data.setName(Name3.Li);

		sqlManager.insert(data);
		UserData3 dbData = sqlManager.unique(UserData3.class,data.getId());
		Assert.assertTrue(Name3.Li==dbData.getName());

	}

    @Table(name="sys_user")
	@Data
    public static class UserData{
    	@AutoID
    	Integer id;
    	Name name;
	}

	/*使用枚举名存库*/
	enum Name{
		Li("li"),
		Zhang("zhang");
		String str;
		Name(String str){
			this.str = str;
		}

	}


	@Table(name="sys_user")
	@Data
	public static class UserData2{
		@AutoID
		Integer id;
		Name2 name;
	}

	/*使用枚举的某个属性*/

	public  enum Name2{
		Li("li"),
		Zhang("zhang");
		@EnumValue
		String str;
		Name2(String str){
			this.str = str;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}
	}


	@Table(name="sys_user")
	@Data
	public static class UserData3{
		@AutoID
		Integer id;
		@EnumMapping("str")
		Name3 name;
	}

	/*一个来自其他系统，无源码的枚举类*/
	public enum Name3{
		Li("li"),
		Zhang("zhang");
		String str;
		Name3(String str){
			this.str = str;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}
	}






}
