package org.beetl.sql.test;

import java.io.Serializable;

import org.beetl.sql.core.TailBean;

public class AppScanRecord extends TailBean implements Serializable {
    //自增id
    private Long id;
    //企业id
    private String bizId;
    //纬度
    private String latitude;
    //经度
    private String longitude;
    //地理地址
    private String addr;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}

}