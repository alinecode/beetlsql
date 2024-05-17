package org.beetl.sql.jmh.dbvisitor;

import lombok.Data;
import net.hasor.dbvisitor.mapping.Column;
import net.hasor.dbvisitor.mapping.Table;


@Table("sys_user")
@Data
public class DBVisitorSysUser {
	@Column(name = "id",primary = true)
    private Integer id ;
    private String code ;
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
