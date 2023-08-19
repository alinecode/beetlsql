package com.beetl.sql.encrypt.test;

import com.beetl.sql.encrypt.annotation.MD5;

import org.beetl.sql.annotation.builder.Date2Long;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.Version;

import java.util.Date;

@Table(name="my_order")
public class MyTestLog {
	@AutoID
	Integer id;
	String code;
	@MD5(saltProperty = "code")
	String  content;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "MyTestLog{" + "id=" + id + ", code='" + code + '\'' + ", content='" + content + '\'' + '}';
	}
}
