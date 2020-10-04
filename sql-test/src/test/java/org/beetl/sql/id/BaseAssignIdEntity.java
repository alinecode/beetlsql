/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2020/9/29 下午10:09
 */

package org.beetl.sql.id;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;

import java.io.Serializable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/9/29
 * 指定主键值
 */
@Data
public abstract class BaseAssignIdEntity<ID extends Serializable>{

    @AssignID("uuid")
    protected ID id;

}
