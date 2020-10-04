package org.beetl.sql.druid;

import lombok.Data;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;

@Table(name="wikipedia")
@Data
public class Wikipedia {
    String channel;
    String comment;
    Integer count;
    @Column("__time")
    Timestamp time;
}
