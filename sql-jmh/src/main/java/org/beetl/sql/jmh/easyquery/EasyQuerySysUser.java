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
	private String code1;
	private String code2;
	private String code3;
	private String code4;
	private String code5;
	private String code6;
	private String code7;
	private String code8;
	private String code9;
	private String code10;
	private String code11;
	private String code12;
	private String code13;
	private String code14;
	private String code15;
	private String code16;
	private String code17;
	private String code18;
	private String code19;
	private String code20;
}
