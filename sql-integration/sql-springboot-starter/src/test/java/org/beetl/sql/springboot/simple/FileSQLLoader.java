package org.beetl.sql.springboot.simple;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.loader.AbstractSQLLoader;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.core.loader.MarkdownParser;
import org.beetl.sql.core.loader.SQLFileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileSQLLoader extends AbstractSQLLoader {

	protected String sqlRoot;
	protected String charset;
	protected Map<SqlId, SQLSource> sqlSourceMap;
	protected SQLSource EMPTY;

	public FileSQLLoader(String root, String charset) {
		this.sqlSourceMap = new ConcurrentHashMap();
		this.EMPTY = SQLSource.emptySource();
		this.sqlRoot = root;
		this.charset = charset;
	}

	public FileSQLLoader(String root) {
		this.sqlSourceMap = new ConcurrentHashMap();
		this.EMPTY = SQLSource.emptySource();
		this.sqlRoot = root;
		this.charset = Charset.defaultCharset().name();
	}

	@Override
	public SQLSource querySQL(SqlId id) {
		return super.querySQL(id);
	}

	@Override
	public SQLSource queryExternalSource(SqlId id) {
		SQLSource source = (SQLSource)this.sqlSourceMap.get(id);
		if (source == this.EMPTY) {
			return null;
		} else if (source != null) {
			return source;
		} else {
			this.loadFromFilePath(id);
			source = (SQLSource)this.sqlSourceMap.computeIfAbsent(id, (key) -> {
				return this.EMPTY;
			});
			return source == this.EMPTY ? null : source;
		}
	}

	@Override
	public boolean existExternalSource(SqlId id) {
		SQLSource source = this.queryExternalSource(id);
		return source != null;
	}

	@Override
	public boolean isExternalSourceModified(SqlId id) {
		SQLSource source = (SQLSource)this.sqlSourceMap.get(id);
		if (source == null) {
			return false;
		} else {
			long oldRootVersion = source.getVersion().root;
			long oldDbVersion = source.getVersion().db;
			File db;
			if (oldRootVersion != 0L) {
				db = this.getRootFile(id);
				return this.getURLVersion(db) != oldRootVersion;
			} else if (oldDbVersion != 0L) {
				db = this.getDBRootFile(id);
				return this.getURLVersion(db) != oldDbVersion;
			} else {
				return false;
			}
		}
	}

	@Override
	public void removeExternalSource(SqlId id) {
		this.sqlSourceMap.remove(id);
	}

	protected Long getURLVersion(File sqlFile) {
		if (sqlFile == null || !sqlFile.exists()) {
			return 0L;
		} else {
			return sqlFile.lastModified();
		}
	}

	protected void loadFromFilePath(SqlId id) {
		File ins = this.getRootFile(id);
		this.readSqlFile(id, ins, true);
		ins = this.getDBRootFile(id);
		this.readSqlFile(id, ins, false);
	}

	protected void readSqlFile(SqlId sqlId, File sqlFile, boolean isRoot) {
		if (sqlFile != null && sqlFile.exists()) {

			String modelName = sqlId.getNamespace();
			long lastModified = this.getURLVersion(sqlFile);
			BufferedReader bf = null;

			try {
				bf = FileUtil.getReader(sqlFile, CharsetUtil.charset(this.charset));
				SQLFileParser parser = this.getParser(modelName, bf);
				SQLSource source = null;

				while((source = parser.next()) != null) {
					if (!this.sqlSourceMap.containsKey(source.getId())) {
						source.sqlType = SQLType.UNKNOWN;
						FileSQLLoader.CustomSQLFileVersion version = new FileSQLLoader.CustomSQLFileVersion();
						version.url = sqlFile.toURI().toURL();
						if (isRoot) {
							version.root = lastModified;
						} else {
							version.db = lastModified;
						}

						source.setVersion(version);
						this.sqlSourceMap.put(source.getId(), source);
					}
				}
			} catch (IOException var21) {
				throw new IllegalStateException(var21);
			} finally {
				if (bf != null) {
					try {
						bf.close();
					} catch (IOException var19) {
						var19.printStackTrace();
					}
				}

			}

		}
	}

	protected File getRootFile(SqlId id) {
		File url = this.getFilePath(this.sqlRoot, id);
		return url;
	}

	protected File getDBRootFile(SqlId id) {
		if(this.dbs == null){
			this.dbs = new MySqlStyle();
		}
		String root = this.sqlRoot + "/" + this.dbs.getName();
		File url = this.getFilePath(root, id);
		return url;
	}

	protected File getFilePath(String root, SqlId id) {
		String path = this.getPathBySqlId(id);
		String filePath0 = root + "/" + path + ".sql";
		String filePath1 = root + "/" + path + ".md";
		File is = this.getFile(filePath0);
		if (!is.exists()) {
			is = this.getFile(filePath1);
		}

		return is;
	}

	private File getFile(String filePath) {
		return new File(filePath);
	}

	@Override
	public String toString() {
		return this.sqlRoot;
	}

	@Override
	public boolean existNamespace(SqlId id) {
		File root = this.getRootFile(id);
		if (root != null && root.exists()) {
			return true;
		} else {
			File db = this.getDBRootFile(id);
			return db != null && db.exists();
		}
	}



	@Override
	public BeetlSQLException getException(SqlId sqlId) {
		String path = this.getPathBySqlId(sqlId);
		String envInfo = path + ".md(sql) sqlLoader:" + this;
		if (this.existNamespace(sqlId)) {
			envInfo = envInfo + ",文件找到，但没有对应的sqlId";
		} else {
			envInfo = envInfo + ",未找到对应的sql文件";
		}

		return new BeetlSQLException(2, "未能找到" + sqlId + "对应的sql,搜索路径:" + envInfo);
	}

	protected SQLFileParser getParser(String modelName, BufferedReader br) throws IOException {
		MarkdownParser parser = new MarkdownParser(modelName, br);
		return parser;
	}


	public String getCharset() {
		return this.charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public void refresh() {
		super.refresh();
		this.sqlSourceMap.clear();
	}

	public static class CustomSQLFileVersion extends MarkdownClasspathLoader.SQLFileVersion {

	}
}
