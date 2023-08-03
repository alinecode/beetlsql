package org.beetl.sql.jmh.easyquery;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.Table;
import lombok.Data;

/**
 * create time 2023/8/3 08:39
 * 文件说明
 *
 * @author xuejiaming
 */
@Data
@Table("sys_user")
public class EasyQuerySysUser {
	@Column(primaryKey = true)
	private Integer id;
	private String code;
}
