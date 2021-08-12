package org.beetl.sql.clazz.kit;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import javax.swing.plaf.nimbus.State;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sql分页辅助工具，最新版采用sqlParser
 * @author xiandafu
 * @author 比尔盖茨("https://gitee.com/git1700_admin")
 * @since  3.5
 * @see SQLManagerExtend
 */
@Plugin
public  class PageKit {
	/* 对于静态sql，string是常量，不会被回收，对于动态拼接的sql，在不使用情况下回收*/
	protected Map<String, String> cache=  null;
	public PageKit(){
		WeakHashMap<String, String>tempMap =new WeakHashMap();
		cache = Collections.synchronizedMap(tempMap);
	}


    public  String getCountSql(String dbName,String selectSql) {
		String countSql = null;
    	try {
    		countSql = cache.get(selectSql);
			if(countSql != null){
				return countSql ;
			}
			Select select = (Select)CCJSqlParserUtil.parse(selectSql, parser -> parser.withSquareBracketQuotation(true));
			if (select.getSelectBody() instanceof PlainSelect) {// 非UNION
				PlainSelect plain = (PlainSelect) select.getSelectBody();
				if (plain.getFromItem() != null) {// 有from
					// 判断select是否仅存在net.sf.jsqlparser.schema.Column类型
					if (plain.getSelectItems().stream().allMatch(item -> ((item instanceof SelectExpressionItem) && (((SelectExpressionItem) item).getExpression() instanceof Column)))) {
						if (plain.getDistinct() == null && plain.getGroupBy() == null && plain.getLimit() == null) {// 非DISTINCT, 非groupBy, 非limit
							plain.setSelectItems(Arrays.asList(new PageKit.CountAll()));
							plain.setOrderByElements(null);
							countSql =  plain.toString();
						}
					}
				}
			}
		} catch (JSQLParserException parserException) {
			//不抛异常，jsqparser有问题，采样默认处理,参考 https://gitee.com/xiandafu/beetlsql/issues/I425LQ
		}
		// 默认处理
		countSql =  "SELECT COUNT(*) FROM ( " + selectSql + " ) a";
		cache.put(selectSql,countSql);
		return countSql;


    }
    
    public static void main(String[] args) throws JSQLParserException {
    	String sql = "SELECT CONVERT(1 USING gbk)";
		PageKit pageKit = new PageKit();
		long start = System.currentTimeMillis();
		String countSql = null;
		for(int i=0;i<1;i++){
			 countSql = pageKit.getCountSql("xxx",sql);
		}
		System.out.println(System.currentTimeMillis()-start);
		System.out.println(countSql);

    }
    public static  class CountAll  extends AllColumns {
    	public String toString(){
    		return "count(*)";
		}
	}

}
