package org.beetl.sql.taos;

import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;

@Table(name="t")
@lombok.Data
public class Data {
    @Column("ts")
    Timestamp ts;
    @Column("a")
    Integer a;
}
