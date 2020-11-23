package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchOne;

@Data
@Table(name="person")
@Fetch
public class Person {
	private Integer id;
	private String address;
	private Integer userId;
	@FetchOne("userId")
	private MyUser user;
}
