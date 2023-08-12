package com.beetl.sql.dynamic;

import org.beetl.core.GroupTemplate;
import org.beetl.core.misc.ByteClassLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.gen.SourceBuilder;
import org.beetl.sql.gen.SourceConfig;
import org.beetl.sql.gen.simple.*;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicTableLoader<T> {
	SQLManager sqlManager;
	Map<String,Class<? extends  T>> cache = new ConcurrentHashMap<>();

	private static Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
	private  static Map<String, JavaFileObject> fileObjectMap = new ConcurrentHashMap<>();


	private String pkg;
	private String baseObject;

	public DynamicTableLoader(SQLManager sqlManager){

		this(sqlManager,"com.test001",(Class<T>)BaseObject.class);
	}
	ByteClassLoader loader = null;


	/**
	 *
	 * @param sqlManager
	 * @param pkg  动态表生成java类的包名
	 * @param clazz 动态表生成java类的父类，如BaseObject，也可以是别的任何类
	 */
	public DynamicTableLoader(SQLManager sqlManager,String pkg,Class<T> clazz){
		this.sqlManager = sqlManager;
		this.pkg = pkg;
		this.baseObject = clazz.getName();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() != null
			? Thread.currentThread().getContextClassLoader()
			: GroupTemplate.class.getClassLoader();
		loader = new ByteClassLoader(classLoader);
	}

	public Class<? extends  T> getDynamicClass(String table){
		Class<? extends  T> c = cache.get(table);
		if(c!=null){
			return c;
		}
		c = cache.computeIfAbsent(table, new Function<String, Class<? extends T>>() {
			@Override
			public Class<? extends T> apply(String s) {
				Class<? extends  T> newCLass =  compile(s);
				return newCLass;
			}
		});
		return c;
	}

	protected Class<? extends  T> compile(String table){
		List<SourceBuilder> sourceBuilder = new ArrayList<>();
		SourceBuilder entityBuilder = new EntitySourceBuilder();
		sourceBuilder.add(entityBuilder);
		SourceConfig config = new SourceConfig(sqlManager,sourceBuilder);
		config.setEntityParentClass(baseObject);
		StringOnlyProject project = new StringOnlyProject(){
			public   String getBasePackage(String sourceBuilderName){
				return pkg+"."+sourceBuilderName;
			}
		};
		String entityPkg =  pkg+".entity";

		config.gen(table,project);
		//生成的entity类
		String javaCode = project.getContent();

		Class<? extends  T> c = doCompile(entityPkg,javaCode);
		return c;
	}





	protected Class<? extends  T> doCompile(String pkg,String javaCode){
		Matcher matcher = CLASS_PATTERN.matcher(javaCode);
		String className;
		if (matcher.find()) {
			className = matcher.group(1);
		} else {
			throw new IllegalArgumentException("No valid class");
		}

		byte[] classByte = getByte(pkg,className,javaCode);
		Class<? extends  T> c = (Class<? extends  T> )loader.defineClass(pkg+"."+className,classByte);
		return c;
	}

	protected byte[] getByte(String pkg,String className,String javaCode){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> compileCollector = new DiagnosticCollector<>();

		JavaFileManager javaFileManager =
			new TmpJavaFileManager(compiler.getStandardFileManager(compileCollector, null, null));

		// 从源码字符串中匹配类名


		// 把源码字符串构造成JavaFileObject，供编译使用
		JavaFileObject sourceJavaFileObject = new TmpJavaFileObject(className, javaCode);

		Boolean result = compiler.getTask(null, javaFileManager, compileCollector,
			null, null, Arrays.asList(sourceJavaFileObject)).call();
		if(!result){
			throw new IllegalArgumentException("compile error "+compileCollector.getDiagnostics());
		}
		JavaFileObject bytesJavaFileObject = fileObjectMap.get(pkg+"."+className);
		byte[] bs = ((TmpJavaFileObject) bytesJavaFileObject).getCompiledBytes();
		return bs;

	}


	public static class TmpJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
		protected TmpJavaFileManager(JavaFileManager fileManager) {
			super(fileManager);
		}

		@Override
		public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException {
			JavaFileObject javaFileObject = fileObjectMap.get(className);
			if (javaFileObject == null) {
				return super.getJavaFileForInput(location, className, kind);
			}
			return javaFileObject;
		}

		@Override
		public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
			JavaFileObject javaFileObject = new TmpJavaFileObject(className, kind);
			fileObjectMap.put(className, javaFileObject);
			return javaFileObject;
		}
	}

	/**
	 * 用来封装表示源码与字节码的对象
	 */
	public static class TmpJavaFileObject extends SimpleJavaFileObject {
		private String source;
		private ByteArrayOutputStream outputStream;

		/**
		 * 构造用来存储源代码的JavaFileObject
		 * 需要传入源码source，然后调用父类的构造方法创建kind = Kind.SOURCE的JavaFileObject对象
		 */
		public TmpJavaFileObject(String name, String source) {
			super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
			this.source = source;
		}

		/**
		 * 构造用来存储字节码的JavaFileObject
		 * 需要传入kind，即我们想要构建一个存储什么类型文件的JavaFileObject
		 */
		public TmpJavaFileObject(String name, Kind kind) {
			super(URI.create("String:///" + name + Kind.SOURCE.extension), kind);
			this.source = null;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			if (source == null) {
				throw new IllegalArgumentException("source == null");
			}
			return source;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			outputStream = new ByteArrayOutputStream();
			return outputStream;
		}

		public byte[] getCompiledBytes() {
			return outputStream.toByteArray();
		}
	}


}
