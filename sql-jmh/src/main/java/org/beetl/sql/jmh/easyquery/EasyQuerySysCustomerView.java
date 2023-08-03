package org.beetl.sql.jmh.easyquery;

import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.enums.RelationTypeEnum;
import lombok.Data;
import org.beetl.sql.jmh.beetl.vo.BeetlSysOrder;

import java.util.List;

/**
 * create time 2023/8/3 09:20
 * 文件说明
 *
 * @author xuejiaming
 */
@Data
public class EasyQuerySysCustomerView {
	private Integer id;
	private String code;
	private String name;
	@Navigate(value = RelationTypeEnum.OneToMany,targetProperty = "customerId")
	private List<EasyQuerySysOrderView> order;
}
