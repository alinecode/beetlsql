package org.beetl.sql.postgres;

import java.sql.Timestamp;
import java.util.Date;

/**
 * CREATE TABLE MappingBean
(
id bigserial,
"text" text,
money  NUMERIC(6,2),
"date" date,
"time" time,
"timestamp" timestamp,
photo bytea
	
);
 * @author xiandafu
 *
 */
public class MappingBean {
	private long id;
	private String text;
	private double money;
	private Date date;
//	private Date time;
	private Timestamp timestamp;
	private byte[] photo;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
//	public Date getTime() {
//		return time;
//	}
//	public void setTime(Date time) {
//		this.time = time;
//	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	@Override
	public String toString() {
		return "MappingBean [id=" + id + ", text=" + text + "]";
	}
	
	
}
