package com.beetl.sql.rewrite.mapper;

import com.beetl.sql.rewrite.annotation.DisableRewrite;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.internal.*;

import java.util.List;

/**
 * 同baseMapper，所有经过RewriteBaseMapper执行的sql，会开启sql重写
 * @param <T>
 * @see RewriteMapperJava8Proxy
 */
public interface RewriteBaseMapper<T> extends BaseMapper<T> {


}
