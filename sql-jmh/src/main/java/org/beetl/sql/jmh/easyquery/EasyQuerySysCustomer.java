package org.beetl.sql.jmh.easyquery;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * create time 2023/8/3 09:13
 * 文件说明
 *
 * @author xuejiaming
 */
@Data
@Table("sys_customer")
public class EasyQuerySysCustomer {
	@Column(primaryKey = true)
	private Integer id;
	private String code;
	private String name;

	@Navigate(value = RelationTypeEnum.OneToMany,targetProperty = "customerId")
	private List<EasyQueryOrder> order;
}
