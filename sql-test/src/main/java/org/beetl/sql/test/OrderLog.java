package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.builder.Date2Long;
import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;

import java.time.OffsetDateTime;
import java.util.Date;

//@Data
@Table(name="order_log")
//@ResultProvider(AutoJsonMapper.class)
public class OrderLog {
	@AutoID
	Integer orderId;
	Integer age;
	@Version
	Integer version;

	@Date2Long
	Date createTime;

	String status;

	@Column("a_bc")
	String aBc;
//	String ABc;
//
//	public String getaBc() {
//		return aBc;
//	}
//
//	public void setaBc(String aBc) {
//		this.aBc = aBc;
//	}

//	public String getABc() {
//		return ABc;
//	}
//
//	public void setABc(String ABc) {
//		this.ABc = ABc;
//	}


	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getaBc() {
		return aBc;
	}

	public void setaBc(String aBc) {
		this.aBc = aBc;
	}

	@Override
	public String toString() {
		return "OrderLog{" + "orderId=" + orderId + ", age=" + age + ", version=" + version + ", createTime="
				+ createTime + ", status='" + status + '\'' + ", aBc='" + aBc + '\'' + '}';
	}
}
