package org.beetl.sql.core.call;

import lombok.Data;

@Data
public class CallParam {
	boolean in;
	int index;
	String name;
}
