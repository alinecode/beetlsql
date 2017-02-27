package org.beetl.sql.test;
import java.math.*;
import java.util.Date;
import java.sql.Timestamp;

/*
* 
* gen by beetlsql 2016-08-20
*/
public class ProductOrder  {
	
	private Integer id ;
	private Integer userId ;
	private String orderName ;
	
	public ProductOrder() {
	}
	
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public Integer getUserId(){
		return  userId;
	}
	public void setUserId(Integer userId ){
		this.userId = userId;
	}
	
	public String getOrderName(){
		return  orderName;
	}
	public void setOrderName(String orderName ){
		this.orderName = orderName;
	}
	

}
