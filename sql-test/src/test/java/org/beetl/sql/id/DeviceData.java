package org.beetl.sql.id;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="device_data")
public class DeviceData  extends  BaseAssignIdEntity<String>{
	String data;
}
