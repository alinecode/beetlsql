package org.beetl.sql.test;

import java.util.Date;

import org.beetl.sql.core.annotatoin.Table;
@Table(name="place_gymnasium")
public class PlaceGymnasium  {
	
	//IDï¼Œå”¯ä¸€ç¼–å�·
	private Integer id ;
	//ä¿±ä¹�éƒ¨ID
	private Integer clubId ;
	//åˆ›å»ºè€…id
	private Integer createId ;
	//åœºåœ°çš„ä¸ªæ•°
	private Integer fieldcount ;
	//æœªå®¡æ ¸ 0 é€šè¿‡ 1  ä¸�é€šè¿‡ 2
	private Integer hasAudit ;
	//è®°å½•æ˜¯å�¦è¢«åˆ é™¤ï¼Œ0:æœªåˆ é™¤ï¼Œ1:å·²ç»�åˆ é™¤
	private Integer isDelete ;
	//è®°å½•æ˜¯å�¦å�¯ç”¨ï¼Œ1:å�¯ç”¨ï¼Œ0:ä¸�å�¯ç”¨
	private Integer isEnable ;
	//è�”ç³»äººä¼šå‘˜id
	private Integer linkerId ;
	//è®°å½•ç‰ˆæœ¬ï¼Œé”�
	private Integer lockVersion ;
	//è®¿é—®æ•°é‡�
	private Integer number ;
	//æ˜¯å�¦æŽ¨è�� 0å�¦1æ˜¯
	private Integer recommend ;
	//é”€é‡�
	private Integer sales ;
	//è®°å½•çŠ¶æ€�å€¼
	private Integer state ;
	//è¯¦ç»†åœ°å�€
	private String address ;
	//æ‰€åœ¨åœ°åŒºçš„ç¼–å�·ç»„
	private String areaIds ;
	//æ‰€åœ¨åŒºåŸŸ 
	private String areaName ;
	//é¢„å®šåœºé¦†çš„è¯´æ˜Ž
	private String booknote ;
	//åœºé¦†ç‰¹è‰²
	private String characteristic ;
	//åˆ›å»ºè€…
	private String createBySid ;
	//å®¡æ ¸ä¸�é€šè¿‡çš„ç�†ç”±
	private String dishasAuditreason ;
	//åŸŸå��
	private String domain ;
	//ä¼šæ‰€è�”ç³»äººå��ç§°
	private String linkerName ;
	//ä¼šæ‰€è�”ç³»äººç”µè¯�
	private String linkerPhone ;
	//å›¾ç‰‡çš„åœ°å�€
	private String logo ;
	//ä¼šæ‰€å��ç§°
	private String name ;
	//ä¼šæ‰€å…¬å‘Š
	private String notice ;
	//æŽ¨è��ç�†ç”±
	private String recommendedreason ;
	//æ‰€åœ¨åŒºåŸŸ
	private String region ;
	//åŒºåŸŸID
	private String regionID ;
	//å¤‡æ³¨ä¿¡æ�¯
	private String remarks ;
	//è�¥ä¸šæ—¶é—´
	private String shopTime ;
	//å­—ç¬¦åž‹ç¼–å�·
	private String sid ;
	//ä¼šæ‰€ç®€ä»‹
	private String summary ;
	//äº¤é€šæ–¹å¼�
	private String traffic ;
	//æ›´æ–°è€…
	private String updateBySid ;
	//è®°å½•åˆ›å»ºæ—¶é—´
	private Date createTime ;
	//è®°å½•æœ€å�Žä¿®æ”¹æ—¶é—´
	private Date modifyTime ;
	

	
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public Integer getClubId(){
		return  clubId;
	}
	public void setClubId(Integer clubId ){
		this.clubId = clubId;
	}
	
	public Integer getCreateId(){
		return  createId;
	}
	public void setCreateId(Integer createId ){
		this.createId = createId;
	}
	
	public Integer getFieldcount(){
		return  fieldcount;
	}
	public void setFieldcount(Integer fieldcount ){
		this.fieldcount = fieldcount;
	}
	
	public Integer getHasAudit(){
		return  hasAudit;
	}
	public void setHasAudit(Integer hasAudit ){
		this.hasAudit = hasAudit;
	}
	
	public Integer getIsDelete(){
		return  isDelete;
	}
	public void setIsDelete(Integer isDelete ){
		this.isDelete = isDelete;
	}
	
	public Integer getIsEnable(){
		return  isEnable;
	}
	public void setIsEnable(Integer isEnable ){
		this.isEnable = isEnable;
	}
	
	public Integer getLinkerId(){
		return  linkerId;
	}
	public void setLinkerId(Integer linkerId ){
		this.linkerId = linkerId;
	}
	
	public Integer getLockVersion(){
		return  lockVersion;
	}
	public void setLockVersion(Integer lockVersion ){
		this.lockVersion = lockVersion;
	}
	
	public Integer getNumber(){
		return  number;
	}
	public void setNumber(Integer number ){
		this.number = number;
	}
	
	public Integer getRecommend(){
		return  recommend;
	}
	public void setRecommend(Integer recommend ){
		this.recommend = recommend;
	}
	
	public Integer getSales(){
		return  sales;
	}
	public void setSales(Integer sales ){
		this.sales = sales;
	}
	
	public Integer getState(){
		return  state;
	}
	public void setState(Integer state ){
		this.state = state;
	}
	
	public String getAddress(){
		return  address;
	}
	public void setAddress(String address ){
		this.address = address;
	}
	
	public String getAreaIds(){
		return  areaIds;
	}
	public void setAreaIds(String areaIds ){
		this.areaIds = areaIds;
	}
	
	public String getAreaName(){
		return  areaName;
	}
	public void setAreaName(String areaName ){
		this.areaName = areaName;
	}
	
	public String getBooknote(){
		return  booknote;
	}
	public void setBooknote(String booknote ){
		this.booknote = booknote;
	}
	
	public String getCharacteristic(){
		return  characteristic;
	}
	public void setCharacteristic(String characteristic ){
		this.characteristic = characteristic;
	}
	
	public String getCreateBySid(){
		return  createBySid;
	}
	public void setCreateBySid(String createBySid ){
		this.createBySid = createBySid;
	}
	
	public String getDishasAuditreason(){
		return  dishasAuditreason;
	}
	public void setDishasAuditreason(String dishasAuditreason ){
		this.dishasAuditreason = dishasAuditreason;
	}
	
	public String getDomain(){
		return  domain;
	}
	public void setDomain(String domain ){
		this.domain = domain;
	}
	
	public String getLinkerName(){
		return  linkerName;
	}
	public void setLinkerName(String linkerName ){
		this.linkerName = linkerName;
	}
	
	public String getLinkerPhone(){
		return  linkerPhone;
	}
	public void setLinkerPhone(String linkerPhone ){
		this.linkerPhone = linkerPhone;
	}
	
	public String getLogo(){
		return  logo;
	}
	public void setLogo(String logo ){
		this.logo = logo;
	}
	
	public String getName(){
		return  name;
	}
	public void setName(String name ){
		this.name = name;
	}
	
	public String getNotice(){
		return  notice;
	}
	public void setNotice(String notice ){
		this.notice = notice;
	}
	
	public String getRecommendedreason(){
		return  recommendedreason;
	}
	public void setRecommendedreason(String recommendedreason ){
		this.recommendedreason = recommendedreason;
	}
	
	public String getRegion(){
		return  region;
	}
	public void setRegion(String region ){
		this.region = region;
	}
	
	public String getRegionID(){
		return  regionID;
	}
	public void setRegionID(String regionID ){
		this.regionID = regionID;
	}
	
	public String getRemarks(){
		return  remarks;
	}
	public void setRemarks(String remarks ){
		this.remarks = remarks;
	}
	
	public String getShopTime(){
		return  shopTime;
	}
	public void setShopTime(String shopTime ){
		this.shopTime = shopTime;
	}
	
	public String getSid(){
		return  sid;
	}
	public void setSid(String sid ){
		this.sid = sid;
	}
	
	public String getSummary(){
		return  summary;
	}
	public void setSummary(String summary ){
		this.summary = summary;
	}
	
	public String getTraffic(){
		return  traffic;
	}
	public void setTraffic(String traffic ){
		this.traffic = traffic;
	}
	
	public String getUpdateBySid(){
		return  updateBySid;
	}
	public void setUpdateBySid(String updateBySid ){
		this.updateBySid = updateBySid;
	}
	
	public Date getCreateTime(){
		return  createTime;
	}
	public void setCreateTime(Date createTime ){
		this.createTime = createTime;
	}
	
	public Date getModifyTime(){
		return  modifyTime;
	}
	public void setModifyTime(Date modifyTime ){
		this.modifyTime = modifyTime;
	}
	

}