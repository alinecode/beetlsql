package org.beetl.sql.sample.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Table(name="department")
@Data
public class DepartmentEntity {
    Integer id;
    String name;
}
