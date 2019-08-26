package org.beetl.sql.core.query.value;

import java.util.Collection;

/**
 * @author GavinKing
 * @ClassName: VerifyValue
 * @Description:校验value是否有效
 * @date 2019/8/26
 */
public class ValueVerify {

    private ValueVerify() {
    }

    private static class InnerClass {
        private static ValueVerify valueVerify = new ValueVerify();
    }

    public static ValueVerify getInstance() {
        return InnerClass.valueVerify;
    }

    /**
     * value是否是一个有效的值
     * @return
     */
    public boolean isEffective(StrongValue strongValue) {
        AssertTypeEnum assertType = strongValue.getAssertType();
        Object value = strongValue.getValue();

        //没有校验类型，认为有效
        if(assertType == null){
            return true;
        }
        //校验NULL值
        if (AssertTypeEnum.NULL.equals(assertType)) {
            return value != null;
        }
        //校验空值
        if (AssertTypeEnum.EMPTY.equals(assertType)) {
            if (value instanceof String) {
                return value != null && !"".equals(value);
            }

            if (value instanceof Collection) {
                return value != null && !((Collection) value).isEmpty();
            }
        }

        return true;
    }
}
