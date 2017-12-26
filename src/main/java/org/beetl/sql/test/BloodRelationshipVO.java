package org.beetl.sql.test;


import java.sql.Timestamp;

import org.beetl.sql.core.annotatoin.Table;

@Table(name="blood_relationship1")
public class BloodRelationshipVO  {
	
		private Integer id ;
		private Integer childrenCount0 ;
		private Integer childrenCount1 ;
		private Integer childrenCount2 ;
		private Integer childrenCount3 ;
		private Integer childrenCount4 ;
		private Integer childrenCount5 ;
		private Integer childrenCount6 ;
		private Integer childrenCount7 ;
		private Integer leaderLevel ;
		private String recommendId ;
		private String userId ;
		private Timestamp createTime ;
		private Timestamp updateTime ;
	
	private String tableName;
	
	private Integer oldLeaderLevel ;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getChildrenCount0() {
		return childrenCount0;
	}
	public void setChildrenCount0(Integer childrenCount0) {
		this.childrenCount0 = childrenCount0;
	}
	public Integer getChildrenCount1() {
		return childrenCount1;
	}
	public void setChildrenCount1(Integer childrenCount1) {
		this.childrenCount1 = childrenCount1;
	}
	public Integer getChildrenCount2() {
		return childrenCount2;
	}
	public void setChildrenCount2(Integer childrenCount2) {
		this.childrenCount2 = childrenCount2;
	}
	public Integer getChildrenCount3() {
		return childrenCount3;
	}
	public void setChildrenCount3(Integer childrenCount3) {
		this.childrenCount3 = childrenCount3;
	}
	public Integer getChildrenCount4() {
		return childrenCount4;
	}
	public void setChildrenCount4(Integer childrenCount4) {
		this.childrenCount4 = childrenCount4;
	}
	public Integer getChildrenCount5() {
		return childrenCount5;
	}
	public void setChildrenCount5(Integer childrenCount5) {
		this.childrenCount5 = childrenCount5;
	}
	public Integer getChildrenCount6() {
		return childrenCount6;
	}
	public void setChildrenCount6(Integer childrenCount6) {
		this.childrenCount6 = childrenCount6;
	}
	public Integer getLeaderLevel() {
		return leaderLevel;
	}
	public void setLeaderLevel(Integer leaderLevel) {
		this.leaderLevel = leaderLevel;
	}
	public String getRecommendId() {
		return recommendId;
	}
	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Integer getOldLeaderLevel() {
		return oldLeaderLevel;
	}
	public void setOldLeaderLevel(Integer oldLeaderLevel) {
		this.oldLeaderLevel = oldLeaderLevel;
	}
	public Integer getChildrenCount7() {
		return childrenCount7;
	}
	public void setChildrenCount7(Integer childrenCount7) {
		this.childrenCount7 = childrenCount7;
	}
}
