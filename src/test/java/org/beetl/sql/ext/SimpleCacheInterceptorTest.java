package org.beetl.sql.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.test.mysql.entity.User;
import org.junit.Assert;
import org.junit.Test;



/**
 * SimpleCacheInterceptorTest.
 */
public class SimpleCacheInterceptorTest {

	@Test
	public void simple() throws Exception {
		List<String> lcs = new ArrayList<String>();
		lcs.add("user");
		SimpleCacheInterceptor sci =new SimpleCacheInterceptor(lcs);
		String ns = "user";
		String selectSqlId = ns + ".select";
		String selectSql = "SELECT appUser.USER_ID \"id\" ,appUser.USER_CODE \"code\" FROM app_user"
				+ " WHERE appUser.USER_ID = ?";
		List<Object> params = new ArrayList<Object>();
		params.add("fitz");
		InterceptorContext ctx = new InterceptorContext(selectSqlId, selectSql, params, null, false);
		String namespace = sci.getSqlIdNameSpace(selectSqlId);
		Assert.assertEquals(ns, namespace);
		Object cacheKey = sci.getCacheKey(ctx);

		// 初次放缓存进对象.
		
		sci.before(ctx);
		Assert.assertNull(ctx.getResult());
		ctx.setResult("fitz");
		sci.after(ctx);
		Assert.assertEquals("fitz", sci.getCacheObject(ns, cacheKey).toString());

		// 第二次应该从缓存获取对象.
		ctx.setResult(null);
		sci.before(ctx);
		Assert.assertNotNull(ctx.getResult());
		Assert.assertEquals("fitz", ctx.getResult().toString());

		// 执行一次更新操作,缓存应该清空.
		String updateSqlId = ns + ".update";
		InterceptorContext ctxUpdate = new InterceptorContext(updateSqlId, "update some_field", params, null, true);
		sci.before(ctxUpdate);
		sci.after(ctxUpdate);
		Assert.assertFalse(sci.containCache(ns, cacheKey));

	}
}
