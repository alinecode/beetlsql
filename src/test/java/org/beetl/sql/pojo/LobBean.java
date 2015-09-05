/**
 * 
 */
package org.beetl.sql.pojo;

import java.util.Arrays;

import org.beetl.sql.core.annotatoin.AutoID;

/**
 * @author suxinjie
 *
 */
public class LobBean {

	private int id;
	private String article;
	private byte[] picture;

	
	@AutoID public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		return "LobBean [id=" + id + ", article=" + article + ", picture="
				+ Arrays.toString(picture) + "]";
	}

}
