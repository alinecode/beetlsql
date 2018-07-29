package org.beetl.sql.ext.gen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO代码生成配置
 */
public class GenConfig {
	//基类，默认就是Object
	private String baseClass;
	//格式控制，4个隔空
	private int spaceCount = 4;
	// double 类型采用BigDecimal
	private boolean preferBigDecimal = true;
	//采用java.util.Date
	private boolean preferDate = true;
	//输出包名
	private String outputPackage = "com.test";

	//默认模板路径
	private final static String defaultTemplatePath = "/org/beetl/sql/ext/gen/pojo.btl";

	private String encoding = "UTF-8";

	/**
	 * 模板
	 */
	private String template = null;
	/**
	 * 是否实现序列化
	 */
	private boolean implSerializable = false;
	/**
	 * 忽略表名前缀
	 */
	private String ignorePrefix = "";

	/**
	 * 使用默认模板
	 */
	public GenConfig() {
		this(defaultTemplatePath);
	}

	/**
	 * 使用自定义模板
	 *
	 * @param templatePath
	 */
	public GenConfig(String templatePath) {
		initTemplate(templatePath);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setSpaceCount(int spaceCount) {
		this.spaceCount = spaceCount;
	}

	public String getOutputPackage() {
		return outputPackage;
	}

	public void setOutputPackage(String outputPackage) {
		this.outputPackage = outputPackage;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	/**
	 * 同时生成其他代码，比如Mapper
	 */
	public List<CodeGen> codeGens = new ArrayList<CodeGen>();

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	//对于数字，优先使用封装类型
//	private boolean preferPrimitive = false ;

	private boolean display = false;

	public String space = "    ";

	private int propertyOrder = ORDER_BY_TYPE;

	public static final int ORDER_BY_TYPE = 1;
	public static final int ORDER_BY_ORIGNAL = 2;

	public GenConfig setBaseClass(String baseClass) {
		this.baseClass = baseClass;
		return this;
	}

	public GenConfig setSpace(int count) {
		this.spaceCount = count;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(" ");
		}
		space = sb.toString();
		return this;
	}

	public GenConfig preferBigDecimal(boolean prefer) {
		this.preferBigDecimal = prefer;
		return this;
	}

	public GenConfig preferPrimitive(boolean primitive) {
		this.preferBigDecimal = primitive;
		return this;
	}

	public String getBaseClass() {
		return baseClass;
	}

	public int getSpaceCount() {
		return spaceCount;
	}

	public boolean isPreferBigDecimal() {
		return preferBigDecimal;
	}

	public boolean isPreferDate() {
		return preferDate;
	}

	public void setPreferDate(boolean preferDate) {
		this.preferDate = preferDate;
	}

	public void setPreferBigDecimal(boolean preferBigDecimal) {
		this.preferBigDecimal = preferBigDecimal;
	}

	public String getSpace() {
		return space;
	}

	public boolean isDisplay() {
		return display;
	}

	public GenConfig setDisplay(boolean display) {
		this.display = display;
		return this;
	}

	/**
	 * 使用模板文件的classpath来初始化模板
	 *
	 * @param classPath
	 */
	private void initTemplate(String classPath) {
		template = getTemplate(classPath);
	}

	/**
	 * mapper 代码生成
	 *
	 * @param classPath
	 * @since 2.6.1
	 */

	public String getTemplate(String classPath) {
		try {
			//系统提供一个pojo模板
			InputStream ins = GenConfig.class.getResourceAsStream(classPath);
			if(ins==null) {
			    ClassLoader loader = Thread.currentThread().getContextClassLoader();
			    if(loader!=null) {
			        ins = loader.getResourceAsStream(classPath);
			    }
			}
			if(ins==null) {
			    throw new RuntimeException("未在classpath下找到Pojo模板文件 "+classPath);
			}
			InputStreamReader reader = new InputStreamReader(ins, this.encoding);
			try{
				//todo, 根据长度来，不过现在模板不可能超过8k
				char[] buffer = new char[1024 * 8];
				int len = reader.read(buffer);
				return new String(buffer, 0, len);
			}finally {
				reader.close();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public int getPropertyOrder() {
		return propertyOrder;
	}

	public void setPropertyOrder(int propertyOrder) {
		this.propertyOrder = propertyOrder;
	}

    public boolean isImplSerializable() {
        return implSerializable;
    }

    public void setImplSerializable(boolean implSerializable) {
        this.implSerializable = implSerializable;
    }

	public String getIgnorePrefix() {
		return ignorePrefix;
	}

	public void setIgnorePrefix(String ignorePrefix) {
		this.ignorePrefix = ignorePrefix;
	}
}
