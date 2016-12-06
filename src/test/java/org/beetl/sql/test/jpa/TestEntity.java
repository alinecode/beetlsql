/*
 * Copyright (C) 2014 lzg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.beetl.sql.test.jpa;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.test.mysql.BaseMySqlTest;

/**
 *
 * @author linziguan@live.com
 * 
 * 
 */
/* ddl
 CREATE TABLE `pf_test` (
  `id` varchar(40) NOT NULL,
  `password` varchar(40) DEFAULT NULL,
  `age` tinyint(3) DEFAULT NULL,
  `ttSize` varchar(50) DEFAULT NULL,
  `bigger` blob,
  `login_name` varchar(40) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `biggerClob` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
 */
@Table(name = "PF_TEST")
public class TestEntity implements Serializable {
	public static String S="SSS";
	@Id
    private String id;
	@Column(name = "login_name",columnDefinition = "VARCHAR")
    private String loginName;
    private String password;
    private Integer age;
    private Long ttSize;
    private byte[] bigger;
    private String biggerClob;
    @Transient
    private String biggerStr;

    /**
     * @return the id
     * @AssignID("uuidSample") TODO加填id的值有问题
     */
    @AssignID
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the loginName
     */
    
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * @return the ttSize
     */
    public Long getTtSize() {
        return ttSize;
    }

    /**
     * @param ttSize the ttSize to set
     */
    public void setTtSize(Long ttSize) {
        this.ttSize = ttSize;
    }

    /**
     * @return the bigger
     */
    public byte[] getBigger() {
        return bigger;
    }

    /**
     * @param bigger the bigger to set
     */
    public void setBigger(byte[] bigger) {
        this.bigger = bigger;
    }

    /**
     * @return the biggerStr
     */
    public String getBiggerStr() {
        return biggerStr;
    }

    /**
     * @param biggerStr the biggerStr to set
     */
    public void setBiggerStr(String biggerStr) {
        this.biggerStr = biggerStr;
    }

	public String getBiggerClob() {
		return biggerClob;
	}

	public void setBiggerClob(String biggerClob) {
		this.biggerClob = biggerClob;
	}

}
