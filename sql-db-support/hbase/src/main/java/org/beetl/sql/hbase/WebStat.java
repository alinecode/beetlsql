package org.beetl.sql.hbase;

import lombok.Data;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Date;

@Data
@Table(name="web_stat")
public class WebStat {
    String host;
    String domain;
    String feature;

    Date date;
    @Column("USAGE.CORE")
    Long core;
    @Column("USAGE.DB")
    Long db;
    @Column("STATS.ACTIVE_VISITOR")
    Integer activeVisitor;

}
