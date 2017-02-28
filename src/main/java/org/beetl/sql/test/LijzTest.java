package org.beetl.sql.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.beetl.sql.core.annotatoin.AssignID;

/*
* 
* gen by beetlsql 2017-01-22
*/
public class LijzTest   implements Serializable{
	private Integer id1 ;
	private Integer id2 ;
	private String name ;
	private Date createTime ;
	private BigDecimal balance;
	private char[] content;
	
	public LijzTest() {
	}
	
	@AssignID
	public Integer getId1(){
		return  id1;
	}
	@AssignID
	public void setId1(Integer id1 ){
		this.id1 = id1;
	}
	
	public Integer getId2(){
		return  id2;
	}
	public void setId2(Integer id2 ){
		this.id2 = id2;
	}
	
	public String getName(){
		return  name;
	}
	public void setName(String name ){
		this.name = name;
	}
	
	public Date getCreateTime(){
		return  createTime;
	}
	public void setCreateTime(Date createTime ){
		this.createTime = createTime;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public char[] getContent() {
		return content;
	}

	public void setContent(char[] content) {
		this.content = content;
	}
	
	
	

}