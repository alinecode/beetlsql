package org.beetl.sql.couch;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;
import java.util.Date;

@Table(name="beer-sample")
@Data
public class BeerSample {
    @AssignID
    @Column("Document.id")
    String id;
    String name;
    String code;
    Date updated;

}
