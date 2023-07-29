package org.beetl.sql.jmh.flex;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;


@Data
@Table(value = "sys_user")
public class FlexSysUser {

	@Id(keyType= KeyType.None)
    private Integer id;
    private String code;


}
