package org.beetl.sql.clazz.kit;

/**
 * 代替 Constants 类. <BR>
 * create time : 2017-05-26 14:32
 *
 * @author luoyizhu@gmail.com
 */
public enum AutoSQLEnum {

    SELECT_BY_ID("$selectById"),
    SELECT_BY_IDS("$selectByIds"),
    SELECT_BY_TEMPLATE("$selectByTemplate"),
    SELECT_COUNT_BY_TEMPLATE("$selectCountByTemplate"),
    DELETE_BY_ID("$delById"),
    SELECT_ALL("$selectAll"),
    UPDATE_ALL("$updateAll"),
    UPDATE_BY_ID("$updateById"),
    UPDATE_TEMPLATE_BY_ID("$updateTemplateById"),
    INSERT("$insert"),
    INSERT_TEMPLATE("$insertTemplate"),
    DELETE_TEMPLATE_BY_ID("$deleteTemplateById"),
    LOCK_BY_ID("$selectByIdForUpdate"),
	EXIST_BY_ID("$existById"),
    ;

    private final String classSQL;

    AutoSQLEnum(String classSQL) {
        this.classSQL = classSQL;
    }

    public String getClassSQL() {
        return classSQL;
    }
}
