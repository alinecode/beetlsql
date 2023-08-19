package com.beetl.sql.encrypt.test;

import com.beetl.sql.encrypt.annotation.AES;
import com.beetl.sql.encrypt.annotation.DES;
import com.beetl.sql.encrypt.annotation.MD5;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="my_order")
public class MyTestLog2 {
	@AutoID
	Integer id;
	String code;
	@AES
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
