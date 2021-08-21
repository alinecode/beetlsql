package org.beetl.sql.core.engine.template;


import org.beetl.core.Configuration;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.concat.ConcatBuilder;
import org.beetl.sql.core.loader.SQLLoader;

import java.util.Properties;

/**
 * beetl模板引擎实现
 */
public class BeetlTemplateEngine implements SQLTemplateEngine {

    Beetl beetl = null;

    StringTemplateResourceLoader tempLoader = new StringTemplateResourceLoader();


    public String STATEMENT_START;// 定界符开始符号
    public String STATEMENT_END;// 定界符结束符号
    public String HOLDER_START;// 站位符开始符号
    public String HOLDER_END;// 站位符结束符号

    @Override
    public void init(SQLLoader loader, Properties ps) {
        beetl = new Beetl(loader, ps);

        Configuration cf = beetl.getGroupTemplate().getConf();
        STATEMENT_START = cf.getStatementStart();
        STATEMENT_END = cf.getStatementEnd();
        if (STATEMENT_END == null || STATEMENT_END.length() == 0) {
            STATEMENT_END = System.getProperty("line.separator", "\n");
        }
        HOLDER_START = cf.getPlaceholderStart();
        HOLDER_END = cf.getPlaceholderEnd();
    }

    @Override
    public SQLTemplate getSqlTemplate(SqlId id) {
        Template template = beetl.getGroupTemplate().getTemplate(id);
        return new BeetlSQLTemplate(template);
    }

    @Override
    public SQLTemplate getSqlTemplate(SqlId id, TemplateContext parent) {
        BeetlTemplateContext context = (BeetlTemplateContext) parent;
        Template template = beetl.getGroupTemplate().getTemplate(id, context.ctx);
        return new BeetlSQLTemplate(template);
    }


    @Override
    public SQLErrorInfo validate(String sqlTemplate) {
        StringTemplateResourceLoader templateResourceLoader = new StringTemplateResourceLoader();
        BeetlException exception = this.beetl.getGroupTemplate().validateTemplate(sqlTemplate, templateResourceLoader);
        if (exception == null) {
            //没有问题的模板
            return null;
        }
        /**
         * 注意，错误行数提示没有纠正到相对于markdown文件位置，而是模板本生位置,如果想得到相对于模板文件位置的错误行数
         * 参考
         * BeetlSQLTemplateExceptionHandler
         */
        ErrorInfo error = new ErrorInfo(exception);
        SQLErrorInfo sqlErrorInfo = new SQLErrorInfo();
        sqlErrorInfo.setLine(error.getErrorTokenLine());
        sqlErrorInfo.setToken(error.getErrorTokenText());
        sqlErrorInfo.setRoot(error.getCause());
        return sqlErrorInfo;
    }

    @Override
    public void genVar(ConcatBuilder concatBuilder, String var) {
        appendVar(concatBuilder, var);
    }

    @Override
    public String appendVar(String express) {
        return HOLDER_START + express + HOLDER_END + " ";
    }

    @Override
    public void genTrimStart(ConcatBuilder concatBuilder) {
        appendStatement(concatBuilder, "trim({suffixOverrides:','}){");
    }

    @Override
    public void genTrimEnd(ConcatBuilder concatBuilder) {
        appendStatement(concatBuilder, "}");
    }

    @Override
    public void genIfNotEmptyStart(ConcatBuilder concatBuilder, String var) {
        appendStatement(concatBuilder, "if(isNotEmpty(" + var + ")){");
    }

    @Override
    public void genIfNotEmptyEnd(ConcatBuilder concatBuilder) {
        appendStatement(concatBuilder, "}");
    }

    @Override
    public void genTestVar(ConcatBuilder concatBuilder, String var) {
        appendVar(concatBuilder, "db.testNull(" + var + "!,\"" + var + "\")");
    }

    @Override
    public void genTestVar(ConcatBuilder concatBuilder, String var, String col) {
        if (col.startsWith("'")) {
            appendVar(concatBuilder, "db.testColNull(" + var + ",\"" + col + "\")");
        } else {
            appendVar(concatBuilder, "db.testColNull(" + var + ",'" + col + "')");
        }
    }
	@Override
	public
	void genTestVarOrDefault(ConcatBuilder concatBuilder,String var,String defaultValue){

    	appendVar(concatBuilder, "db.testVarNull(" + var + ",\"" + defaultValue + "\")");

	}

    @Override
    public String wrapString(String str) {
        if (str.startsWith(STATEMENT_START)) {
            return "\\" + str;
        } else {
            return str;
        }
    }

    protected void appendVar(ConcatBuilder concatBuilder, String express) {
        concatBuilder.append(HOLDER_START).append(express).append(HOLDER_END).append(" ");
    }

    protected void appendStatement(ConcatBuilder concatBuilder, String statement) {
        concatBuilder.append(STATEMENT_START).append(statement).append(STATEMENT_END);
    }

    public Beetl getBeetl() {
        return beetl;
    }
}
