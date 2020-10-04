package org.beetl.sql.id;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="device")
public class Device extends  BaseAutoIdEntity<Integer>{
	String sn;
}
