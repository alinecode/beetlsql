package org.beetl.sql.id;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Seq;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="big_data")
public class BigData {
	@AssignID
	Integer orderId;
	@AssignID
	Integer status;
	@Auto
	Integer dataId;
	@Seq(name="label_sequence")
	Integer label;
}
