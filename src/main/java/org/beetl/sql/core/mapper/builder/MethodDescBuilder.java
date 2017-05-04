package org.beetl.sql.core.mapper.builder;

import org.beetl.sql.core.mapper.MethodDesc;

/**
 * 创建 MethodDesc, 独立出这个构造器是为了让用户灵活改造 MethodDesc <BR>
 * create time : 2017-04-27 15:47
 *
 * @author luoyizhu@gmail.com
 */
public interface MethodDescBuilder {
    MethodDesc create();
}
