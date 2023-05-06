package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Table(name="user")
@Data
public class QuickSubUser extends QuickUser{
	String name ;
}
