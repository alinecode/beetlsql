package org.beetl.sql.xml;

import org.beetl.core.*;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.core.tag.GeneralVarTagBinding;
import org.beetl.core.tag.Tag;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.TrimTag;
import org.beetl.sql.core.engine.WhereTag;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.ext.PluginExtConfig;


import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * sqlManager支持xml，需要调用support方法。
 *
 * xml语法高仿mybatis语法，支持但不限于
 * if,where,include,foreach,bind，trim
 * 提供额外的xml包括
 * isEmpty，isNotEmpty，isBlank，isNotBlank
 * <pre>
 * XMLBeetlSQL xmlSupport = new XMLBeetlSQL();
 * xmlSupport.config(sqlManager);
 *
 *
 * </pre>
 *
 * @see "QuickXMLTestBeetlSQL"
 */
public class XMLBeetlSQL implements PluginExtConfig
{
	public static Set<String> holderSet = new HashSet<>();
	static{
		//这些标签+属性名需要使用beetl表达式 比如<if test="a" > 变成 <if test=#{a} >
		holderSet.add("if_test");
		holderSet.add("foreach_items");
		holderSet.add("bind_value");
	}
	@Override
	public void config(SQLManager sqlManager) {
		BeetlTemplateEngine beetlSQLTemplateEngine = (BeetlTemplateEngine) sqlManager.getSqlTemplateEngine();
		GroupTemplate gt = beetlSQLTemplateEngine.getBeetl().getGroupTemplate();
		//支持xml标签
		String htmlTagStart = "<b:" ;
		String  htmlTagEnd = "</b:";
		Configuration.HtmlTagHolder tagHolder = new Configuration.HtmlTagHolder(htmlTagStart,htmlTagEnd,"var",true);
		gt.getConf().setTagConf(tagHolder);
		registerXMLTag(gt);
		// xml标签实现类，所有xml标记都被beetl转化为标签函数 htmltag(){...}
		gt.registerTag("htmltag", XMLTagSupportWrapper.class);
	}

	public  void registerTag(SQLManager sqlManager,String name,Tag tag){
		BeetlTemplateEngine beetlSQLTemplateEngine = (BeetlTemplateEngine) sqlManager.getSqlTemplateEngine();
		GroupTemplate gt = beetlSQLTemplateEngine.getBeetl().getGroupTemplate();
		gt.registerTag("name", IfTag.class);
	}

	private  void registerXMLTag(GroupTemplate groupTemplate){
		groupTemplate.registerTag("if", IfTag.class);
		groupTemplate.registerTag("include", Include.class);
		groupTemplate.registerTag("isNotEmpty", IsNotEmpty.class);
		groupTemplate.registerTag("isEmpty", IsEmpty.class);
		groupTemplate.registerTag("isBlank", isBlank.class);
		groupTemplate.registerTag("isNotBlank", IsNotBlank.class);
		groupTemplate.registerTag("foreach", Foreach.class);
		groupTemplate.registerTag("bind", BindTag.class);
		groupTemplate.registerTag("trim", TrimTag.class);
		groupTemplate.registerTag("where", XMLWhereTag.class);
	}



	public  class XMLWhereTag extends WhereTag {

		@Override
		protected void initTrimArgs(Object[] args) {
			this.prefix = "WHERE";
			this.prefixOverrides = new String[]{"AND ", "OR "};
		}
	}

	/**
	 * 定义一个变量，变量的名称是export属性决定，参考beetl的
	 */
	public static class BindTag extends GeneralVarTagBinding {
		@Override
		public void render() {
			Object value = this.getAttributeValue("value");
			this.binds(value);
		}
	}


	public static class IsEmpty extends  GeneralEmptyTag{
		protected   String getFunction(){
			return "isEmpty";
		}
	}

	public static class IsNotEmpty extends  GeneralEmptyTag{
		protected   String getFunction(){
			return "isNotEmpty";
		}
	}

	public static class IsNotBlank extends  GeneralEmptyTag{
		protected   String getFunction(){
			return "isNotBlank";
		}
	}

	public static class isBlank extends  GeneralEmptyTag{
		protected   String getFunction(){
			return "isBlank";
		}
	}

	public static abstract class GeneralEmptyTag extends Tag{

		static StringTemplateResourceLoader stringTemplateResourceLoader = new StringTemplateResourceLoader();
		@Override
		public void render() {
			String express =  (String)getHtmlAttribute("value");
			StringWriter sw = new StringWriter();
			Map paras = (Map)ctx.globalVar;
			String function = getFunction();
			Map ret = gt.runScript("return "+function+"("+express+");",paras,sw,stringTemplateResourceLoader);
			if(ret.isEmpty()){
				throw new BeetlSQLException(BeetlSQLException.ERROR,"执行表达式错误 "+express+" error:"+sw.toString());
			}
			Boolean o = (Boolean)ret.get("return");
			if(o){
				doBodyRender();
			}
		}

		protected  abstract String getFunction();

	}

	public static class Include extends  Tag{

		@Override
		public void render() {
			//复用已经有的方法,useFunction,globalUseFunction,忽略可添加额外参数功能，
			String id = (String)super.getHtmlAttribute("refid");
			Function include = null;
			Object[] paras = null;
			if(id.lastIndexOf(".")==-1){
				include = gt.getFunction("use");
				paras = new Object[]{id};


			}else{
				include = gt.getFunction("globalUse");
				paras = new Object[]{id};
			}

			ByteWriter writer = ctx.byteWriter;
			ByteWriter tempWriter = ctx.byteWriter.getTempWriter(writer);
			ctx.byteWriter = tempWriter;
			include.call(new Object[]{id},ctx);
			ctx.byteWriter = writer;
			try {
				ctx.byteWriter.fill(tempWriter);
			} catch (IOException e) {
				//ingore
				throw new IllegalStateException(e);
			}

		}
	}

	public static class IfTag extends Tag{

		@Override
		public void render() {

			if (!containHtmlAttribute("test")) {
				throw new IllegalArgumentException("缺少 test属性");
			}
			Object value = this.getHtmlAttribute("test");
			if (!(value instanceof Boolean)) {
				throw new IllegalArgumentException("期望test表达式运算结果是boolean类型");
			}
			if ((Boolean) value) {
				this.doBodyRender();
			}
		}
	}

	public static class Foreach extends GeneralVarTagBinding {

		@Override
		public void render() {
			if (!this.containHtmlAttribute("items")) {
				throw new IllegalArgumentException(this.getHtmlTagName() + " 期望 items属性");
			}
			Object value = this.getAttributeValue("items");
			if (value == null) {
				return ;
			}

			//第三个是绑定的列表
			boolean containStatus = false;
			String str = (String) this.args[2];
			if (str.indexOf(',') != -1) {
				containStatus = true;
			}

			ILoopStatus it = GeneralLoopStatus.getIteratorStatus(value);
			if (it == null) {
				throw new RuntimeException("期望数组或者集合，实际类型是:" + value.getClass());
			}
			try{
				if(this.containHtmlAttribute("open")){
					ctx.byteWriter.writeString((String)this.getAttributeValue("open"));
				}

				boolean haSeparator = containHtmlAttribute("separator");
				String separator = (String)getAttributeValue("separator");
				while (it.hasNext()) {
					Object item = it.next();
					if (containStatus) {
						this.binds(item, it);
					} else {
						this.binds(item);
					}
					this.doBodyRender();
					if(haSeparator&&!it.isLast()){
						ctx.byteWriter.writeString(separator);
					}
				}

				if(this.containHtmlAttribute("close")){
					ctx.byteWriter.writeString((String)this.getAttributeValue("close"));
				}
			}catch (IOException ioe){
				//ignore
			}

		}
	}
}
