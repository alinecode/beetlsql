package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.mapper.annotation.CallParam;

@Data
public class OutHolder {
	@CallParam(2)
	String name;

}
