package org.beetl.sql.id;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="device_detail")
public class DeviceDetail extends  BaseSeqIdEntity<Integer>{
	String json;
}
