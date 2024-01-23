package org.beetl.sql.doris;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.SqlResource;
import org.beetl.sql.mapper.annotation.Update;

import java.util.List;
@SqlResource("system.user")
public interface DorisMapper extends BaseMapper<DorisUser> {
     @Sql("delete  from example_tbl_agg1 where user_id = ?")
	 @Update
     void deleteById(Integer id);

	 PageResult<DorisUser> select(PageRequest pageRequest);

}
