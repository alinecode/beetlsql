package org.beetl.sql.clickhouse;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

import java.util.Date;

@Data
@Table(name="test")
public class ClickHouseUser {
    @AssignID
    private String id;
    private String name;
    private Long createTs;
    private Date day;
}
