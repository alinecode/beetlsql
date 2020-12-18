package org.beetl.sql.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.LogicDelete;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.Version;

import java.util.Date;

@Data
@Table(name="product_order")
public class ProductOrder {
    @AutoID
    Integer id;
    Date createDate;
    @Version(1)
    Long version;
    @LogicDelete(1)
    Integer status;

}

