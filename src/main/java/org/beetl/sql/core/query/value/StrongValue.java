package org.beetl.sql.core.query.value;

/**
 * @author GavinKing
 * @ClassName: StrongValue
 * @Description:健壮的查询变量，用于空值，null等多情况判断
 * 如果出现空值等情况则不进行SQL语句的组装
 * @date 2019/8/26
 */
public class StrongValue {
    /***
     * 判断类型
     */
    private AssertTypeEnum assertType;

    /***
     * 变量值
     */
    private Object value;

    public StrongValue(AssertTypeEnum assertType, Object value) {
        this.assertType = assertType;
        this.value = value;
    }

    public AssertTypeEnum getAssertType() {
        return assertType;
    }

    public void setAssertType(AssertTypeEnum assertType) {
        this.assertType = assertType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * value是否是一个有效的值
     * @return
     */
    public boolean isEffective() {
        return ValueVerify.getInstance().isEffective(this);
    }
}
