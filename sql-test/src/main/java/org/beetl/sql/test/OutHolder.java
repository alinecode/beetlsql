package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.mapper.annotation.CallIndex;

@Data
public class OutHolder {
	@CallIndex(2)
	Integer count;
	@CallIndex(3)
	String descName;
}
