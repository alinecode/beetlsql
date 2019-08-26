package org.beetl.sql.core.query.value;

/**
 * @author GavinKing
 * @ClassName: AssertTypeEnum
 * @Description:断言类型
 * 对查询value的判断情况枚举
 * @date 2019/8/26
 */
public enum AssertTypeEnum {
    //空指针判断
    NULL("ASSERT_NULL"),
    //空值与空指针判断
    EMPTY("ASSERT_EMPTY"),
    ;

    private final String type;

    AssertTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
