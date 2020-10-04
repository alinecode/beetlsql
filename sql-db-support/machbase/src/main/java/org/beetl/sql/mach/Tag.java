package org.beetl.sql.mach;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;

@Table(name="tag")
@Data
public class Tag {
    @AssignID
    String name;
    Timestamp time;
    Double value;

}
