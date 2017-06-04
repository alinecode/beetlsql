package org.beetl.sql.test;

import java.io.Serializable;

import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.UpdateIgnore;
import org.beetl.sql.core.annotatoin.Version;

public class Credit   implements Serializable{
	@AutoID
	private Integer id ;
	private Integer balance ;
	
	private Integer version ;

	private String name;
	
	public Credit() {
	}
	
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public Integer getBalance(){
		return  balance;
	}
	public void setBalance(Integer balance ){
		this.balance = balance;
	}
	@Version
	public Integer getVersion(){
		return  version;
	}
	public void setVersion(Integer version ){
		this.version = version;
	}
	@UpdateIgnore
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	

}