package org.beetl.sql.firewall;

import lombok.Data;
import lombok.experimental.Accessors;
import org.beetl.sql.core.SqlId;

import java.util.HashSet;
import java.util.Set;
@Data
@Accessors(chain = true)
public class FireWall {
	private boolean deletedUnLimit = false;
	private boolean updateUnLimit = false;
	private boolean truncateEnable = false;

	private boolean dmlCreateEnable = true;
	private boolean dmlAlterEnable = false;
	private boolean dmlDropEnable = false;

	private Set<SqlId> whiteList = new HashSet<>();



	private int sqlMaxLength= 0;

	/**
	 * 0 拒绝，1 告警
	 */
	private int action =0 ;
}
