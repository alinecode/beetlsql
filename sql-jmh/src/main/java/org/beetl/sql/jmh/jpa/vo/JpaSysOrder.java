package org.beetl.sql.jmh.jpa.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_order")
@Data
@Entity
public class JpaSysOrder {
    @Id
    private Integer id;
    private String name;
    @Column(name="customer_id")
    private Integer customerId;
}
