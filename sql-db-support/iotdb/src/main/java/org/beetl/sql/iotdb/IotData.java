package org.beetl.sql.iotdb;

import lombok.Data;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table(name= IotData.TABLE)
@Data
public class IotData {
	public final static String TABLE="root.test2.wf01.wt01";
	@Column("time")
	Timestamp timestamp;
	@Column("status")
	Boolean status;
	@Column("json")
	String json ;
}
