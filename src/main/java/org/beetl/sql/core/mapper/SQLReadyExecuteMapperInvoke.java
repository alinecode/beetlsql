package org.beetl.sql.core.mapper;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.engine.Pageable;
import org.beetl.sql.core.kit.PageKit;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 执行jdbc sql
 *
 * @author xiandafu
 */
public class SQLReadyExecuteMapperInvoke extends BaseMapperInvoke {
    int type;

    public SQLReadyExecuteMapperInvoke(int type) {
        this.type = type;
    }

    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        if (type == 2 || type == 3) {
            MethodDesc desc = MethodDesc.getMetodDesc(sm, entityClass, m, sqlId);
            Class returnType = desc.renturnType;
            List list = sm.execute(new SQLReady(sqlId, args), returnType);
            if (type == 2) {
                return list.size() == 0 ? null : list.get(0);
            } else {

                return list;
            }
        }

        // 分页对象
        if (type == 7) {
            return this.getPage(sm, sqlId, entityClass, args);
        }

        return sm.executeUpdate(new SQLReady(sqlId, args));

    }

    protected <T> Pageable<T> getPage(SQLManager sm, String sql, Class<T> clazz, Object[] args) {

        // 获取count语句
        SQLScript sqlScrip = sm.getScript(sql);
        String sqlCount = PageKit.getCountSql(sqlScrip.getSql());

        // 分页去除前面两个参数 (页码, 每页显示多少)
        Object[] countArgs = null;
        if (args.length > 2) {
            countArgs = new Object[args.length - 2];
            for (int i = 0; i < countArgs.length; i++) {
                countArgs[i] = args[i + 2];
            }
        }

        List<Long> countResultList = sm.execute(new SQLReady(sqlCount, countArgs), Long.class);

        int pageNumber = (Integer) args[0];
        int pageSize = (Integer) args[1];
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

            String pageSql = sm.getDbStyle().getPageSQLStatement(sql, start, size);

            // 添加条件参数
            List<Object> pageConditionList = new LinkedList<Object>();
            if (countArgs != null) {
                for (int i = 0; i < countArgs.length; i++) {
                    pageConditionList.add(countArgs[i]);
                }
            }

            // 把分页参数添加到最后
            pageConditionList.add(start);
            pageConditionList.add(size);

            // 执行查询
            SQLReady sqlReady = new SQLReady(pageSql, pageConditionList.toArray());
            list = sm.execute(sqlReady, clazz);

            // 总页数
            int totalPage = (int) (totalRow / pageSize);
            if (totalRow % pageSize != 0) {
                totalPage++;
            }

            page.setTotalPage(totalPage);

        } else {
            list = Collections.emptyList();
        }

        // 查询结果
        page.setList(list);

        return page;
    }

}
