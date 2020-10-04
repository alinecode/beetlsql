package org.beetl.sql.hive;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;
import java.util.Date;

/**
 *  CREATE  TABLE `dept`(
 *   `dept_no` int,
 *   `addr` string,
 *   `tel` string,
 *   `create_ts` timestamp) ;
 */
@Data
@Table(name="dept")
public class Dept {
    @AssignID
    Integer deptNo;
    String addr;
    String tel;
    Date createTs;
}
