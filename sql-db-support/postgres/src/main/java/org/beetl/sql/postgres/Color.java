package org.beetl.sql.postgres;

import lombok.Data;

@Data
public class Color {
	public Color(String ab, String desc) {
		this.ab = ab;
		this.desc = desc;
	}

	public Color() {

	}

	String ab;
	String desc;
}
