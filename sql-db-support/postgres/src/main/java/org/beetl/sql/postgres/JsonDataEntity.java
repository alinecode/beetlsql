package org.beetl.sql.postgres;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

/**
 * 自动把对象转为json字符串，存入postgres库
 */
@Data
@Table(name="json_test")
public class JsonDataEntity {
	@AssignID
	String id;
	@Jackson
	@Column("json_data")
	Color jsonData;

	@Column("create_ts")
	Long createTs;

}
