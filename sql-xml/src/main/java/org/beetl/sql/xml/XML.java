package org.beetl.sql.xml;

import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.tag.Tag;
import org.beetl.core.tag.TagFactory;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.BeetlSQLTemplateEngine;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;

import javax.swing.text.html.HTML;

public class XML {
	public static void support(SQLManager sqlManager){
		BeetlTemplateEngine beetlSQLTemplateEngine = (BeetlTemplateEngine) sqlManager.getSqlTemplateEngine();
		GroupTemplate gt = beetlSQLTemplateEngine.getBeetl().getGroupTemplate();
		//支持xml标签
		gt.getConf().setHtmlTagSupport(true);
		gt.getConf().setHtmlTagFlag("s:");
		gt.getConf().setHtmlTagBindingAttribute("var");
		registerXMLTag(new GroupTemplate());
	}

	private static void registerXMLTag(GroupTemplate groupTemplate){
		groupTemplate.registerTag("if", IfTag.class);
	}

	public static class IfTag extends Tag{

		public IfTag(){

		}

		@Override
		public void render() {
			boolean test = (Boolean)super.getHtmlAttribute("test");
			if(test){
				super.doBodyRender();
			}else{
				return ;
			}

		}
	}
}
