package org.beetl.sql.ext.jfinal;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="sys_user")
@Data
public class JFinalUser {
	@AutoID
	Long id;
	String name;

}
