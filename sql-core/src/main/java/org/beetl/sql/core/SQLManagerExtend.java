package org.beetl.sql.core;

import org.beetl.sql.clazz.kit.PageKit;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.extend.EnumExtend;
import org.beetl.sql.core.extend.ParaExtend;

/**
 * 一些常见的扩展类，用户可以自己扩展，比如sqlManagaer.getSQLManagerExtend().setEnumExtend(yourextends)
 * EnumExtend： 负责枚举转化
 * ParaExtend： sql执行可以默认添加参数，比如租户
 * PageKit：用于jdbc的分页，默认使用jsqlparser完成
 * @see SQLManagerBuilder
 */
@Plugin
public class SQLManagerExtend {
    protected  EnumExtend enumExtend = new EnumExtend();
	protected ParaExtend paraExtend = new ParaExtend();
	protected PageKit pageKit = new PageKit();

    public SQLManagerExtend(){

    }
    public ParaExtend getParaExtend(){
        return  paraExtend;
    }

    public EnumExtend getEnumExtend(){
        return enumExtend;
    }

    public PageKit getPageKit(){return pageKit;};


	public void setEnumExtend(EnumExtend enumExtend) {
		this.enumExtend = enumExtend;
	}

	public void setParaExtend(ParaExtend paraExtend) {
		this.paraExtend = paraExtend;
	}

	public void setPageKit(PageKit pageKit) {
		this.pageKit = pageKit;
	}
}


