package org.beetl.sql.core;

import org.beetl.sql.clazz.kit.PageKit;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.extend.EnumExtend;
import org.beetl.sql.core.extend.ParaExtend;

/**
 * 一些常见的扩展类
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


}


