package org.beetl.sql.mapper.template;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.builder.MethodParam;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>{@code
 * @SqlTemplate("select * from user where name=#name#")
 * public PageResult<User> queryName(String name,PageRequest request);
 * }
 * </pre>
 */
public class PageTemplateMI extends SelectTemplateMI {
    boolean pageResultRequired;

    public PageTemplateMI(String sql, Class targetType, boolean pageResultRequired, MethodParamsHolder holder) {
        super(sql, targetType, holder, false);
        this.pageResultRequired = pageResultRequired;
    }

    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        PageRequest pageRequest = (PageRequest) args[holder.getPageRequestIndex()];
        PageResult pageResult = sm.executePageQuery(getSqId(sm,entityClass,m),this.getSql(), this.targetType, getParas(args), pageRequest);
        if (pageResultRequired) {
            return pageResult;
        } else {
            return pageResult.getList();
        }
    }

    @Override
    public Object getParas(Object[] paras) {

        if (paras.length == 1) {
            //只有PageRequest请求
            return new HashMap();
        }
        int pageRequestIndex = holder.getPageRequestIndex();

        Map map = new HashMap();
        List<MethodParam> paramList = holder.getParas();
        for (int i = 0; i < paras.length; i++) {
            if (i == pageRequestIndex) {
                continue;
            }
            map.put(paramList.get(i).getParamName(), paras[i]);
        }

        return map;

    }
}
