package org.beetl.sql.usage.mapper.dao;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.*;
import org.beetl.sql.sample.entity.UserEntity;

/**
 * 演示翻页操作
 */
/*md文件名*/
@SqlResource("user")
public interface UserPageMapper extends BaseMapper<UserEntity> {

    /*包含PageRequest的查询认为是翻页查询*/
    PageResult pageQueryByCondition(String name, PageRequest request);

    PageResult pageQueryByCondition2(String name, PageRequest request);
}
