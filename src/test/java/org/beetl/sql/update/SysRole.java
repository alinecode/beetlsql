package org.beetl.sql.update;

import java.util.Date;
import java.io.Serializable;


/**
 * 角色表对象
 * @email: lyg945@163.com
 * @author: Yonggang.Liu
 * @date: 2015-10-22 16:19:22
 */
public class SysRole implements Serializable {
       
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String name;
	
	//系统角色不能删除
	private String sysFlag;
	
	private String appId;
	
	private Integer companyId;
	
	private String miscDesc;
	
	//状态：DICT_GLOBAL_STATUS 有效V、无效I、草稿D、待审核W
	private String status;
	
	private Date createTime;
	
	private Integer createOperId;
	
	private String createOperName;
	
	private Date lastModTime;
	
	private Integer lastModOperId;
	
	private String lastModOperName;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getSysFlag() {
		return sysFlag;
	}

	public void setSysFlag(String sysFlag) {
		this.sysFlag = sysFlag;
	}
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getMiscDesc() {
		return miscDesc;
	}

	public void setMiscDesc(String miscDesc) {
		this.miscDesc = miscDesc;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getCreateOperId() {
		return createOperId;
	}

	public void setCreateOperId(Integer createOperId) {
		this.createOperId = createOperId;
	}
	public String getCreateOperName() {
		return createOperName;
	}

	public void setCreateOperName(String createOperName) {
		this.createOperName = createOperName;
	}
	public Date getLastModTime() {
		return lastModTime;
	}

	public void setLastModTime(Date lastModTime) {
		this.lastModTime = lastModTime;
	}
	public Integer getLastModOperId() {
		return lastModOperId;
	}

	public void setLastModOperId(Integer lastModOperId) {
		this.lastModOperId = lastModOperId;
	}
	public String getLastModOperName() {
		return lastModOperName;
	}

	public void setLastModOperName(String lastModOperName) {
		this.lastModOperName = lastModOperName;
	}

	@Override
	public String toString() {
		return this.id+"";
	}
}
