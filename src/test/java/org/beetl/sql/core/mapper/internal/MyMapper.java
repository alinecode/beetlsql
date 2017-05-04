package org.beetl.sql.core.mapper.internal;

import java.util.List;

/**
 * 不继承beetlsql的 {@link org.beetl.sql.core.mapper.BaseMapper} 也能拥有内置实现 <BR>
 * create time : 2017-04-27 18:12
 *
 * @author luoyizhu@gmail.com
 */
public interface MyMapper<T> {
    long selectCount();

    List<T> selectAll();

    List<Integer> selectIds();
}
