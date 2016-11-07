package org.beetl.sql.ext;

import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.core.InterceptorContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * SimpleCacheInterceptorTest.
 */
public class SimpleCacheInterceptorTest {

	@Test
	public void simple() throws Exception {
		String ns = "com.company.app.entity.AppUser";
		SimpleCacheInterceptor sci = new SimpleCacheInterceptor();
		String selectSqlId = ns + ".select";
		String selectSql = "SELECT appUser.USER_ID \"id\" ,appUser.USER_CODE \"code\" FROM app_user"
				+ " WHERE appUser.USER_ID = ?";
		List<Object> params = new ArrayList<Object>();
		params.add("fitz");
		String namespace = sci.getSqlIdNameSpace(selectSqlId);
		Assert.assertEquals(ns, namespace);
		String cacheKey = sci.getCacheKey(selectSqlId, params);
		Assert.assertFalse(sci.existCacheKey(ns, cacheKey));

		// 初次放缓存进对象.
		InterceptorContext ctx = new InterceptorContext(selectSqlId, selectSql, params, null, false);
		sci.before(ctx);
		Assert.assertNull(ctx.getResult());
		ctx.setResult("fitz");
		sci.after(ctx);
		Assert.assertTrue(sci.existCacheKey(ns, cacheKey));
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
		Assert.assertFalse(sci.existCacheKey(ns, cacheKey));

	}
}
