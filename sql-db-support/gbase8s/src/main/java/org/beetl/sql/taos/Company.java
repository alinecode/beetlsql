package org.beetl.sql.taos;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;

import java.util.Date;

/**
 * DROP TABLE IF EXISTS COMPANY;
 * CREATE TABLE COMPANY(
 *   ID         SERIAL         PRIMARY KEY,
 *   NAME       VARCHAR(40)    NOT NULL,
 *   AGE        INT            NOT NULL,
 *   ADDRESS    CHAR(50),
 *   SALARY     DECIMAL(10,2),
 *   JOIN_DATE  DATE           DEFAULT TODAY
 * );
 */
@Data
public class Company {
	@AutoID
	Integer id;
	String name;
	Integer age;
	Date joinDate;
}
