package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;

@Data
public class DeviceData {
	@AssignID("uuid")
	String id;
	String data;
}
