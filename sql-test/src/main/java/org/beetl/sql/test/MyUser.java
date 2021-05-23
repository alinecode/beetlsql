package org.beetl.sql.test;


import lombok.Data;
import org.beetl.sql.annotation.builder.UpdateTime;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.mapping.stream.StreamResultSetMapper;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchOne;

import java.util.Date;

@Data
@Table(name="sys_user")
@ResultProvider(StreamResultSetMapper.class)
public class MyUser {
    @Auto()
    private Integer id;
    @Column("user_name")
    private String name;

}
