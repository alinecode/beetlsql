package org.beetl.sql.ymtraix;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;
@Data
@Table(name="my_view",isView = true)
public class MyView {
	@AssignID
	String id;
	@Jackson
	@Column("json_data")
	Color jsonData;
}
