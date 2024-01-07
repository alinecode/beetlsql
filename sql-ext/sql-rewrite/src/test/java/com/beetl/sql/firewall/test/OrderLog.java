package com.beetl.sql.firewall.test;

import lombok.Data;

@Data
public class OrderLog {
	Integer id;
	String name;
	Integer tenantId;
}
