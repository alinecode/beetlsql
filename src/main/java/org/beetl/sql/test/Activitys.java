package org.beetl.sql.test;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动表
 */
public class Activitys implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4455700683413230125L;
	/** id主键 */
	private Long id;

	/** 组织id */
	private Long orgId;

	/** 活动名称 */
	private String name;

	/** 活动logo */
	private String logo;

	/** 1 大人和小孩 2 小孩 3大人 */
	private int isMax;

	/** 活动价格 */
	private Double price;

	/***/
	private Double minPrice;

	/***/
	private Double maxPrice;

	/** 活动日期 */
	private Date startDate;

	/***/
	private String startTime;

	/***/
	private Date endDate;

	/***/
	private String endTime;

	/** 活动地点 */
	private String address;

	/** 活动要求 */
	private String requirement;

	/** 活动详情 */
	private String detail;

	/** 活动状态 1,售票中，2，售完 */
	private int status;

	/** 1 是 2 不是 */
	private int isHot;

	/***/
	private String indexSearch;

	/***/
	private String twoIndexSearch;

	/***/
	private int areasSearch;

	/***/
	private Double lng;

	/***/
	private Double lat;

	/** 创建时间 */
	private Date createTime;

	/** 创建人 */
	private String createPerson;

	/** 修改人 */
	private String lastUpdatePerson;

	/** 修改时间 */
	private Date lastUpdateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getIsMax() {
		return isMax;
	}

	public void setIsMax(int isMax) {
		this.isMax = isMax;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRequirement() {
		return requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIsHot() {
		return isHot;
	}

	public void setIsHot(int isHot) {
		this.isHot = isHot;
	}

	public String getIndexSearch() {
		return indexSearch;
	}

	public void setIndexSearch(String indexSearch) {
		this.indexSearch = indexSearch;
	}

	public String getTwoIndexSearch() {
		return twoIndexSearch;
	}

	public void setTwoIndexSearch(String twoIndexSearch) {
		this.twoIndexSearch = twoIndexSearch;
	}

	public int getAreasSearch() {
		return areasSearch;
	}

	public void setAreasSearch(int areasSearch) {
		this.areasSearch = areasSearch;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreatePerson() {
		return createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}