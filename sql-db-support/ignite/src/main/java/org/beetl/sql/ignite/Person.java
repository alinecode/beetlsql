package org.beetl.sql.ignite;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="Person")
@Data
public class Person {
    @AssignID
    Long id;
    String name;
    String country;
}
