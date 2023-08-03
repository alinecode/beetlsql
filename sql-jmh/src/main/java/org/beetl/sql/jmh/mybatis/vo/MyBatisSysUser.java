package org.beetl.sql.jmh.mybatis.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;

@TableName("sys_user")
@Data
public class MyBatisSysUser {

    @TableId(type= IdType.ASSIGN_ID)
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
