package org.beetl.sql.doris;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Table(name="example_tbl_agg1")
public class DorisUser {

	@AssignID
    private Integer userId;
    private Date date;
    private String city;
	private Integer age;
	private Integer sex;
    private Timestamp lastVisitDate;
    private Long cost;
    private Integer maxDwellTime;
    private Integer minDwellTime;
}
