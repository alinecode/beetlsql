package org.beetl.sql.jmh.jpa.vo;

import lombok.Data;

import javax.persistence.*;


@Entity
@Data
@Table(name="sys_user")
public class JpaSysUser {
    @Id
    private Integer id ;
    @Column()
    private String code;

}
