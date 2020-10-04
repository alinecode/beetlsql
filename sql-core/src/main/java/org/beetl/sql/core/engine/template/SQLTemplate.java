package org.beetl.sql.core.engine.template;

import java.util.Map;

/**
 * 定义一个sql模板实现类，Beetlsql默认使用beetl实现，有可以替换成其他模板引擎
 * @author xiandafu
 */
public interface SQLTemplate {
    public void setPara(String name,Object value);
    public void setParas(Map map);

    /**
     * 渲染模板，得到sql语句
     * @return
     */
    public String render();

    public TemplateContext getContext();



}
