package org.beetl.sql.jmh.mybatis.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;

@TableName("sys_user")
@Data
public class MyBatisSysUser {

    @TableId(type= IdType.ASSIGN_ID)
    private Integer id;
    private String code;


}
