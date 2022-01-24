package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 从classpath系统加载sql模板，id应该格式是"xx.yyy",xx代表了文件名，yyy代表了sql标识 sql 模板格式如下：
 *
 * <pre>
 * selectUser
 * ===
 *
 * select * from user
 * </pre>
 * <p>
 * 可以重载此类，实现{@code #getParser}  用于解析其他格式文件，比如xml格式的sql文件
 *
 * @author Administrator
 */
@Plugin
public class MarkdownClasspathLoader extends AbstractClassPathSQLLoader {

    protected String sqlRoot;
    protected String charset;

    /**
     * 外部sql缓存
     */
    protected Map<SqlId, SQLSource> sqlSourceMap = new ConcurrentHashMap<>();

    protected SQLSource EMPTY = SQLSource.emptySource();

    public MarkdownClasspathLoader(String root, String charset) {
        super();
        this.sqlRoot = root;
        this.charset = charset;

    }

    public MarkdownClasspathLoader(String root) {
        super();
        this.sqlRoot = root;
        this.charset = Charset.defaultCharset().name();

    }

    public MarkdownClasspathLoader() {
        this("sql");

    }

    @Override
    public SQLSource queryExternalSource(SqlId id) {
        SQLSource source = sqlSourceMap.get(id);

        if (source == EMPTY) {
            return null;
        }
        if (source != null) {
            return source;
        }
        //从未被加载过

        loadFromClassPath(id);
        source = sqlSourceMap.computeIfAbsent(id, key -> EMPTY);
        if (source == EMPTY) {
            return null;
        } else {
            return source;
        }

    }


    @Override
    public boolean existExternalSource(SqlId id) {
        SQLSource source = queryExternalSource(id);
        return source != null;
    }

    /**
     * 比较sql是否变化，比较sql所在的文件是否变化，如果变化，则认为sql变化，提示beetl重新解析sql语句
     *
     * @param id
     * @return
     */
    @Override
    public boolean isExternalSourceModified(SqlId id) {

        SQLSource source = this.sqlSourceMap.get(id);
        if (source == null) {
            return false;
        }

        long oldRootVersion = source.getVersion().root;
        long oldDbVersion = source.getVersion().db;

        if(oldRootVersion!=0){
        	//认为这个sqlId是root的md文件
			URL root = this.getRootFile(id);
			return getURLVersion(root) != oldRootVersion;
		}else if(oldDbVersion!=0) {
			URL db = this.getDBRootFile(id);
			return  getURLVersion(db) != oldDbVersion;
		}else{
        	//均为0，md在jar文件，不包含版本变化
        	return false ;
		}

    }

    @Override
    public void removeExternalSource(SqlId id) {
        this.sqlSourceMap.remove(id);

    }


    protected Long getURLVersion(URL url) {
        if (url == null) {
            return 0L;
        }

        if ("file".equals(url.getProtocol())) {
            String path = url.getFile();
            return new File(path).lastModified();
        } else {
            //其他协议，比如jar。
            return 0L;
        }
    }


    /***
     *  考虑到跨数据库支持，ClasspathLoader加载SQL顺序如下：
     首先根据DBStyle.getName() 找到对应的数据库名称，然后在ROOT/dbName 下找对应的sql，
     如果ROOT/dbName 文件目录不存在，或者相应的sql文件不存在，再搜索ROOT目录下的sql文件。
     如mysql 里查找user.select2,顺序如下：
     - 先找ROOT/mysql/user.sql 文件，如果有此文件，且包含了select2，则返回此sql语句，
     - 如果没有，下一步查找ROOT/mysql/user.md,如果有此文件，且包含了slect2，则返回sql语句
     - 如果没有，下一步查找ROOT/user.sql,如果有此文件，且包含了slect2，则返回sql语句
     - 如果没有，下一步查找ROOT/user.md,如果有此文件，且包含了slect2，则返回sql语句
     - 都没有，抛错，告诉用户未在ROOT/,或者ROOT/mysql 下找到相关sql
     *
     * @return
     */
    protected void loadFromClassPath(SqlId id) {
        //读取root目录下的sql文件
        URL ins = this.getRootFile(id);
        readSqlFile(id, ins, true);
        //读取db目录下的sql文件，进行覆盖
        ins = this.getDBRootFile(id);
        readSqlFile(id, ins, false);

    }

    protected void readSqlFile(SqlId sqlId, URL url, boolean isRoot) {
        if (url == null) {
            return;
        }
        InputStream ins;
        try {
            ins = url.openStream();
        } catch (IOException e1) {
            return;
        }

        String modelName = sqlId.getNamespace();
        long lastModified = getURLVersion(url);
        BufferedReader bf = null;
        try {

            bf = buildBufferedReader(ins, charset);
            //解析markdown,可以解析xml，需要实现SQLFileParser
            SQLFileParser parser = this.getParser(modelName, bf);
            SQLSource source = null;
            while ((source = parser.next()) != null) {
                source.sqlType = SQLType.UNKNOWN;
                SQLFileVersion version = new SQLFileVersion();
                version.url = url;
                if (isRoot) {
                    version.root = lastModified;
                } else {
                    version.db = lastModified;
                }
                source.setVersion(version);
                sqlSourceMap.put(source.getId(), source);
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    // 忽略
                    e.printStackTrace();
                }
            }
        }
        return;
    }


    /***
     * 获取.md文件
     * md文件需放在classpath下
     * @param id
     * @return
     */
    protected URL getRootFile(SqlId id) {
        URL url = getFilePath(sqlRoot, id);
        return url;

    }

    protected URL getDBRootFile(SqlId id) {
        String root = sqlRoot + "/" + dbs.getName();
        URL url = getFilePath(root, id);
        return url;
    }


    protected URL getFilePath(String root, SqlId id) {
        String path = this.getPathBySqlId(id);
        String filePath0 = root + "/" + path + ".sql";
        String filePath1 = root + "/" + path + ".md";
        URL is = this.getFile(filePath0);
        if (is == null) {
            is = this.getFile(filePath1);
        }
        return is;
    }


    private URL getFile(String filePath) {
        return classLoaderKit.loadResourceAsURL(filePath);

    }

    @Override
    public String toString() {
        return this.sqlRoot;
    }

    @Override
    public boolean existNamespace(SqlId id) {
        URL root = this.getRootFile(id);
        if (root != null) {
            return true;
        }
        URL db = this.getDBRootFile(id);
        return db != null;
    }

	/**
	 * 记录sql的版本号，如果为0，表示无版本，比如md文件在jar里
	 */
	public static class SQLFileVersion {
		/**
		 * sql资源URL表示
		 */
		public URL url;
		/** 根目录下sql文件版本 */
		public long root = 0L;
		/** 具体db下的 */
		public long db = 0L;

		public boolean isModified(SQLFileVersion newVersion) {
			return newVersion.root != root || newVersion.db != db;
		}
	}

    protected SQLFileParser getParser(String modelName, BufferedReader br) throws IOException {
        MarkdownParser parser = new MarkdownParser(modelName, br);
        return parser;
    }

    /**
     * 子类覆盖，可以用于加密文件的解密
     *
     * @param inputStream
     * @param charset
     * @return
     */
    protected BufferedReader buildBufferedReader(InputStream inputStream, String charset) {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, charset));
            return bf;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("错误的charset " + charset);
        }
    }

    public String getSqlRoot() {
        return sqlRoot;
    }

    public void setSqlRoot(String sqlRoot) {
        this.sqlRoot = sqlRoot;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}

