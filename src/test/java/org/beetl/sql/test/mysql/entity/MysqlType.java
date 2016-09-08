package org.beetl.sql.test.mysql.entity;

import java.math.BigDecimal;
import java.util.Date;

public class MysqlType  {
	private Integer id ;
	private Integer age ;
	private Integer gender ;
	private String allContract ;
	private String contract ;
	private BigDecimal money ;
	private byte[] pic ;
	private BigDecimal version ;
	private Date createDate ;
	private Date createTime ;
	
	public MysqlType() {
	}
	
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public Integer getAge(){
		return  age;
	}
	public void setAge(Integer age ){
		this.age = age;
	}
	
	public Integer getGender(){
		return  gender;
	}
	public void setGender(Integer gender ){
		this.gender = gender;
	}
	
	public String getAllContract(){
		return  allContract;
	}
	public void setAllContract(String allContract ){
		this.allContract = allContract;
	}
	
	public String getContract(){
		return  contract;
	}
	public void setContract(String contract ){
		this.contract = contract;
	}
	
	public BigDecimal getMoney(){
		return  money;
	}
	public void setMoney(BigDecimal money ){
		this.money = money;
	}
	
	public byte[] getPic(){
		return  pic;
	}
	public void setPic(byte[] pic ){
		this.pic = pic;
	}
	
	public BigDecimal getVersion(){
		return  version;
	}
	public void setVersion(BigDecimal version ){
		this.version = version;
	}
	
	public Date getCreateDate(){
		return  createDate;
	}
	public void setCreateDate(Date createDate ){
		this.createDate = createDate;
	}
	
	public Date getCreateTime(){
		return  createTime;
	}
	public void setCreateTime(Date createTime ){
		this.createTime = createTime;
	}
	

}