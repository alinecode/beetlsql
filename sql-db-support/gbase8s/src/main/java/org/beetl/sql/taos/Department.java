package org.beetl.sql.taos;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;

/**
 *  CREATE TABLE DEPARTMENT(
 *    ID             INT   PRIMARY KEY,
 *    DEPT           CHAR(50) NOT NULL,
 *    EMP_ID         INT      NOT NULL
 * );
 */
@Data
public class Department {
	@AssignID
	Integer id;
    String dept;
    Integer empId;
}
