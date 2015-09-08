package org.beetl.sql.loader;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLSource;
import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

	@Test
	public void test(){
		ClasspathLoader loader = new ClasspathLoader("/sql");
		SQLSource source = loader.getSQL("user.selectUser");
		Assert.assertNotNull(source);
		
		source = loader.getSQL("subModel1.car.selectUser");
		Assert.assertNotNull(source);
	}
}
