package org.beetl.sql.jmh.jpa.vo;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * orm 测试
 */
@Table(name="sys_customer")
@Data
@Entity
public class JpaSysCustomer {
    @Id
    private Integer id;
    private String code;
    private String name;
    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name="customer_id")
    private List<JpaSysOrder> order;
}
