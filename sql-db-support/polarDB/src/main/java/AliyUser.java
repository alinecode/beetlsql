import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;

import java.util.Date;

@Table(name="user")
@Data
public class AliyUser {
    @AssignID
    Integer id;
    String name;
    Date createDate;
}
