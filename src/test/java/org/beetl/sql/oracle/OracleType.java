package org.beetl.sql.oracle;

import java.sql.Timestamp;
import java.util.Date;

import org.beetl.sql.core.annotatoin.AssignID;

/**
 * CREATE TABLE OracleType(
id number(9),
name varchar2(20),
nvChar  Nvarchar2(20),
nChar Nchar(20),
money  number(5,2),
date1 date,
date2 timestamp,
image BLOB ,
text2 CLOB,
dFloat BINARY_FLOAT,
dDouble BINARY_DOUBLE
 );
 * @author xiandafu
 *
 */
public class OracleType {
	Long id ;
	String name;
	String nvChar;
	String nChar;
	Double money;

	Date date1;
	Timestamp st;
	byte[] image;
	String text2;
//	Float dFloat;
//	Double dDouble;
	
	@AssignID
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNvChar() {
		return nvChar;
	}
	public void setNvChar(String nvChar) {
		this.nvChar = nvChar;
	}
	public String getNChar() {
		return nChar;
	}
	public void setNChar(String nChar) {
		this.nChar = nChar;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}

	public Date getDate1() {
		return date1;
	}
	public void setDate1(Date date1) {
		this.date1 = date1;
	}
	public Timestamp getSt() {
		return st;
	}
	public void setSt(Timestamp st) {
		this.st = st;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
	}
//	public Float getDFloat() {
//		return dFloat;
//	}
//	public void setDFloat(Float dFloat) {
//		this.dFloat = dFloat;
//	}
//	public Double getDDouble() {
//		return dDouble;
//	}
//	public void setDDouble(Double dDouble) {
//		this.dDouble = dDouble;
//	}
	@Override
	public String toString() {
		return "OracleType [id=" + id + ", name=" + name + "]";
	}
	
}
