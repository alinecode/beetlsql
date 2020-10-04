package org.beetl.sql.core.query.interfacer;

/**
 * @author GavinKing
 * @ClassName: StrongValue
 * @Description:健壮的查询变量，用于空值，null等多情况判断,用户可以自定义自己的实现
 * 如果出现空值等情况则不进行SQL语句的组装
 * @date 2019/8/26
 */
public interface StrongValue {

    /**
     * value是否是一个有效的值
     * 返回false则不进行SQL组装
     * @return
     */
    boolean isEffective();

    /**
     * 获取实际的value值
     * @return
     */
    Object getValue();
}
