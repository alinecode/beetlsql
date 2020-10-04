package org.beetl.sql.hbase;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="STOCK_SYMBOL")
@Data
public class Stock {
    @AssignID
    String symbol;
    String company;
}
