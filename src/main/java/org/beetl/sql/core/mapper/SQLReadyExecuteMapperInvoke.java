package org.beetl.sql.core.mapper;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.Pageable;
import org.beetl.sql.core.kit.PageKit;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 执行jdbc sql
 *
 * @author xiandafu, luoyizhu
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

    /**
     * 分页
     * <pre>
     * 通过注解sql实现的分页功能, 必须传入两个参数
     * 参数1: pageNumber
     * 参数2: pageSize
     * 顺序不能乱, 变量名没要求.
     * </pre>
     *
     * @param sm    SQLManager
     * @param sql   正常的sql查询语句
     * @param clazz 需要转换的类型
     * @param args  查询条件
     * @param <T>   T
     * @return 分页对象
     */
    protected <T> Pageable<T> getPage(SQLManager sm, String sql, Class<T> clazz, Object[] args) {

        // 获取count语句
        String sqlCount = PageKit.getCountSql(sql);

        // 分页去除前面两个参数 (页码, 每页显示多少)
        Object[] conditionArgs = null;
        if (args.length > 2) {
            conditionArgs = new Object[args.length - 2];
            for (int i = 0; i < conditionArgs.length; i++) {
                conditionArgs[i] = args[i + 2];
            }
        }

        List<Long> countResultList = sm.execute(new SQLReady(sqlCount, conditionArgs), Long.class);

        int pageNumber = (Integer) args[0];
        int pageSize = (Integer) args[1];
        long totalRow = countResultList.get(0);
        Pageable<T> page = PageKit.createPage();
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setTotalRow((int) totalRow);

        List<T> list;


        if (totalRow != 0) {
            long start = (sm.isOffsetStartZero() ? 0 : 1) + (pageNumber - 1) * pageSize;
            long size = pageSize;

            String pageSql = sm.getDbStyle().getPageSQLStatement(sql, start, size);

            // 执行查询
            SQLReady sqlReady = new SQLReady(pageSql, conditionArgs);
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
