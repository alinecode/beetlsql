package org.beetl.sql.gauss;

import lombok.Data;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;

import java.util.Date;

@Table(name="huawei_db")
@Data
public class HuaweiDb {
    @Auto
    Integer id;
    String name;
    Date createTime;
}
