package org.beetl.sql.test;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.beetl.sql.core.annotatoin.AssignID;



@Entity
@Table(name = "t_s_log")

public class TSLog implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Id
	private String id;
	

	@Column(name = "loglevel")
	private Short loglevel;
	
	
	@Column(name = "operatetime")
	private Date operatetime;
	
	
	@Column(name = "operatetype")
	private Short operatetype;
	
	
	@Column(name = "logcontent")
	private String logcontent;
	
	
	@Column(name = "broswer")
	private String broswer;// 用户浏览器类型
	
	
	@Column(name = "note")
	private String note;
    
	@AssignID("uuid")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Short getLoglevel() {
		return this.loglevel;
	}

	public void setLoglevel(Short loglevel) {
		this.loglevel = loglevel;
	}

	public Date getOperatetime() {
		return this.operatetime;
	}

	public void setOperatetime(Date operatetime) {
		this.operatetime = operatetime;
	}

	public Short getOperatetype() {
		return this.operatetype;
	}

	public void setOperatetype(Short operatetype) {
		this.operatetype = operatetype;
	}

	public String getLogcontent() {
		return this.logcontent;
	}

	public void setLogcontent(String logcontent) {
		this.logcontent = logcontent;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getBroswer() {
		return broswer;
	}

	public void setBroswer(String broswer) {
		this.broswer = broswer;
	}

}