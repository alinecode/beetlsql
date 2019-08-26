package org.beetl.sql.core.query.util;

import org.beetl.sql.core.query.value.AssertTypeEnum;
import org.beetl.sql.core.query.value.StrongValue;

/**
 * @author GavinKing
 * @ClassName: QueryValueUtil
 * @Description:构造StrongValue的工具类
 * @date 2019/8/26
 */
public class QueryUtil {

    /**
     * 过滤空和NULL的值，
     * 如果为空或者null则不增加查询条件
     * @param value
     * @return
     */
    public static StrongValue filterEmpty(Object value) {
        return new StrongValue(AssertTypeEnum.EMPTY, value);
    }

    /**
     * 过滤空和NULL的值，
     * 如果为空或者null则不增加查询条件
     * @param value
     * @return
     */
    public static StrongValue filterNull(Object value) {
        return new StrongValue(AssertTypeEnum.NULL, value);
    }


}
