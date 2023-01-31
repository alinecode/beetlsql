package org.beetl.sql.core.engine;

import org.beetl.core.ConsoleErrorHandler;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;
import org.beetl.core.resource.StringTemplateResource;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SqlId;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BeetlSQLTemplateExceptionHandler extends ConsoleErrorHandler {
	@Override
	public void processException(BeetlException ex, GroupTemplate gt, Writer writer) {
		ErrorInfo error = new ErrorInfo(ex);

		int line = error.getErrorTokenLine();
		StringBuilder sb = null;
		int startLine = 0;
		ResourceLoader resLoader = gt.getResourceLoader();
		Resource res = null;
		//潜在问题，此时可能得到是一个新的模板，不过可能性很小，忽略！

		String content = null;

		Object id = ex.resource.getId();
		if(id instanceof String){
			//临时方案，目前beetsql的模板引擎是groupTemplate，同时使用在文件和字符串 渲染,当字符串时候，需要特殊如下处理
			sb = new StringBuilder(">>").append(getDateTime()).append(":").append(error.getType()).append(":")
					.append(error.getErrorTokenText()).append(" 位于").append(line + startLine - 1).append("行").append(" 资源:")
					.append((String)id);
			 res = new StringTemplateResource((String)id,null);
		}else{
			SqlTemplateResource resource = (SqlTemplateResource) gt.getResourceLoader().getResource(ex.resource.getId());
			startLine = resource.getLine();

			 sb = new StringBuilder(">>").append(getDateTime()).append(":").append(error.getType()).append(":")
					.append(error.getErrorTokenText()).append(" 位于").append(line + startLine - 1).append("行").append(" 资源:")
					.append(getResourceName(ex.resource.getId()));
			 res = resLoader.getResource(ex.resource.getId());
		}



		if (ex.getMessage() != null) {
			println(writer, ex.getMessage());
		}

		try {
			//显示前后三行的内容
			int[] range = this.getRange(line);
			content = res.getContent(range[0], range[1]);
			if (content != null) {
				String[] strs = content.split(ex.cr);
				int lineNumber = range[0];
				for (int i = 0; i < strs.length; i++) {
					print(writer, "" + (lineNumber + startLine - 1));
					print(writer, "|");
					println(writer, strs[i]);
					lineNumber++;
				}

			}
		} catch (IOException e) {

			//ingore

		}

		if (error.hasCallStack()) {
			println(writer, "  ========================");
			println(writer, "  调用栈:");
			for (int i = 0; i < error.getResourceCallStack().size(); i++) {
				Object errorId = error.getResourceCallStack().get(i).getId();
				SqlTemplateResource errorResource = (SqlTemplateResource) gt.getResourceLoader()
						.getResource(errorId);
				startLine = errorResource.getLine();
				println(writer, "  " + errorId + " 行：" + (error.getTokenCallStack().get(i).line + startLine));
			}
		}

		printCause(error, writer);
		try {
			writer.flush();
		} catch (IOException e) {

		}

		throw new BeetlSQLException(BeetlSQLException.SQL_SCRIPT_ERROR, "SQL Script Error:" + sb,ex);
	}

	@Override
	protected String getDateTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(date);
	}

	protected String getResourceName(String resourceId) {
		if (resourceId.length() > 30) {
			return resourceId.substring(0, 30);
		}
		return resourceId;
	}
}
