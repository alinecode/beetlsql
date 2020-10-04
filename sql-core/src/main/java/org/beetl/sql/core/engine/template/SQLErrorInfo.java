package org.beetl.sql.core.engine.template;

import lombok.Data;

/**
 * sql模板运行或者解析后的错误结果
 * @author xiandafu
 */
@Data
public class SQLErrorInfo {
    private int line;
    private String token;
    private Throwable root;

}
