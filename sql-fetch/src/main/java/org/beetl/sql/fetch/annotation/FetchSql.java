package org.beetl.sql.fetch.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.fetch.FetchSqlAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>{@code
 *     @Fetch
 *     public class User{
 *     	  private Long id;
 *     	  private Long departmentId
 *		  @FetchSql("select * from audit_log where user_id=#{id} order by create_time")
 *		  List<AuditLog> logs;
 *     }
 *
 *
 * }</pre>
 * 通过sql来获取额外查询结果
 * 参考 {@link FetchOne}
 * @author xiandafu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(FetchSqlAction.class)
public @interface FetchSql {
	/**
	 * sql模板语句
	 * @return
	 */
	String value();



}


