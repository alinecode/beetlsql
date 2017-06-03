package org.beetl.sql.core.mapper;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.engine.Pageable;
import org.beetl.sql.core.kit.PageKit;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 分页接口处理类
 * create time : 2017-05-28 19:33
 *
 * @author luoyizhu@gmail.com
 */
public class PageableMapperInvoke extends BaseMapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {

        if (Pageable.class.isAssignableFrom(m.getReturnType()) == false) {
            throw new UnsupportedOperationException(m.getName() + " 方法返回值必须是分页接口类型!");
        }

        Map<String, Object> sqlArgs = this.getSqlArgs(sm, entityClass, m, args, sqlId);

        MethodDesc desc = MethodDesc.getMetodDesc(sm, entityClass, m, sqlId);

        return this.getPage(sm, sqlId, desc.renturnType, sqlArgs);
    }

    protected <T> Pageable<T> getPage(SQLManager sm, String sqlId, Class<T> clazz, Map<String, Object> paras) {

        // 获取count语句
        SQLScript sqlScrip = sm.getScript(sqlId);
        String sqlCount = PageKit.getCountSql(sqlScrip.getSql());

        List<Long> countResultList = sm.execute(sqlCount, Long.class, paras);


        int pageNumber = PageKit.getPageNumber(paras);
        int pageSize = PageKit.getPageSize(paras);
        long totalRow = countResultList.get(0);

        Pageable<T> page = PageKit.createPage();
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setTotalRow((int) totalRow);

        List<T> list;
        if (totalRow != 0) {
            boolean offsetStartZero = sm.isOffsetStartZero();

            long start = (offsetStartZero ? 0 : 1) + (pageNumber - 1) * pageSize;
            long size = pageSize;
            list = sm.select(sqlId, clazz, paras, start, size);

            // 总页数
            int totalPage = (int) (totalRow / pageSize);
            if (totalRow % pageSize != 0) {
                totalPage++;
            }

            page.setTotalPage(totalPage);

        } else {
            list = Collections.emptyList();
        }

        page.setList(list);

        return page;
    }
}
