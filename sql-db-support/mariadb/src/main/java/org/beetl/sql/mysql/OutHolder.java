package org.beetl.sql.oceanbase;

import lombok.Data;
import org.beetl.sql.mapper.annotation.CallParam;

@Data
public class OutHolder {
	@CallParam(2)
	Integer id;

}
