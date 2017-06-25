package org.beetl.sql.core.kit;

/**
 * 代替 Constants 类. <BR>
 * create time : 2017-05-26 14:32
 *
 * @author luoyizhu@gmail.com
 */
public enum ConstantEnum {

    SELECT_BY_ID("_gen_selectById"),
    SELECT_BY_TEMPLATE("_gen_selectByTemplate"),
    SELECT_COUNT_BY_TEMPLATE("_gen_selectCountByTemplate"),
    DELETE_BY_ID("_gen_delById"),
    SELECT_ALL("_gen_selectAll"),
    UPDATE_ALL("_gen_updateAll"),
    UPDATE_BY_ID("_gen_updateById"),
    UPDATE_TEMPLATE_BY_ID("_gen_updateTemplateById"),
    INSERT("_gen_insert"),
    INSERT_TEMPLATE("_gen_insertTemplate"),
    DELETE_TEMPLATE_BY_ID("_gen_deleteTemplateById"),
    LOCK_BY_ID("_gen_selectByIdForUpdate"),
    ;

    private final String classSQL;

    ConstantEnum(String classSQL) {
        this.classSQL = classSQL;
    }

    public String getClassSQL() {
        return classSQL;
    }
}
