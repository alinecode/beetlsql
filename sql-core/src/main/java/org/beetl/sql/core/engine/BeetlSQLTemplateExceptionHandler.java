package org.beetl.sql.core.engine;

import org.beetl.core.ConsoleErrorHandler;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;
import org.beetl.sql.clazz.kit.BeetlSQLException;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BeetlSQLTemplateExceptionHandler extends ConsoleErrorHandler {
	//Override
	public void processExcption(BeetlException ex, Writer writer) {
		ErrorInfo error = new ErrorInfo(ex);

		int line = error.getErrorTokenLine();

		SqlTemplateResource resource = (SqlTemplateResource) ex.gt.getResourceLoader().getResource(ex.resource.getId());
		int startLine = resource.getLine();

		StringBuilder sb = new StringBuilder(">>").append(getDateTime()).append(":").append(error.getType()).append(":")
				.append(error.getErrorTokenText()).append(" 位于").append(line + startLine - 1).append("行").append(" 资源:")
				.append(getResourceName(ex.resource.getId()));

		System.out.println(sb.toString());
		if (ex.getMessage() != null) {
			println(writer, ex.getMessage());
		}

		ResourceLoader resLoader = ex.gt.getResourceLoader();
		//潜在问题，此时可能得到是一个新的模板，不过可能性很小，忽略！

		String content = null;
		try {
			Resource res = resLoader.getResource(ex.resource.getId());
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
				SqlTemplateResource errorResource = (SqlTemplateResource) ex.gt.getResourceLoader()
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
