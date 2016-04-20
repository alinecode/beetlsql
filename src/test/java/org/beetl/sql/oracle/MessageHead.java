package org.beetl.sql.oracle;

import java.sql.Timestamp;

import org.beetl.sql.core.annotatoin.SeqID;

public class MessageHead  {
	private String createUserId ;
	private String functionCode ;
	private Long headId ;
	private String lastUpdateUserId ;
	private String messageId ;
	private String messageType ;
	private String receiverId ;
	private String senderId ;
	private String version ;

	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}
	
	@SeqID(name="my_user_seq")
	public Long getHeadId() {
		return headId;
	}
	public void setHeadId(Long headId) {
		this.headId = headId;
	}
	public String getLastUpdateUserId() {
		return lastUpdateUserId;
	}
	public void setLastUpdateUserId(String lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
	

}