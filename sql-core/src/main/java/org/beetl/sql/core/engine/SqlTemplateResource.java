package org.beetl.sql.core.engine;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLLoader;

import java.io.IOException;
import java.io.Reader;

public class SqlTemplateResource extends Resource<SqlId> {

	SQLSource source;

	public SqlTemplateResource(SqlId id, SQLSource source, ResourceLoader loader) {
		super(id, loader);
		this.source = source;

	}

	public Reader openReader() {
		return new NoneBlockStringReader(source.getTemplate());
	}

	@Override
	public boolean isModified() {
		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();
		return loader.isModified(source.getId());

	}

	public String getTemplate() {
		return source.getTemplate();
	}

	public int getLine() {
		return source.getLine();
	}



}

/**
 * 参考StringReader写的，去掉了synchronzied 和 ensureOpen以提高性能
 */
class  NoneBlockStringReader extends Reader {

	private String str;
	private int length;
	private int next = 0;
	private int mark = 0;


	public NoneBlockStringReader(String s) {
		this.str = s;
		this.length = s.length();
	}

	private void ensureOpen() throws IOException {
		return;
	}

	public int read() throws IOException {

		if (next >= length)
			return -1;
		return str.charAt(next++);
	}

	public int read(char cbuf[], int off, int len) throws IOException {
			if ((off < 0) || (off > cbuf.length) || (len < 0) ||
					((off + len) > cbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}
			if (next >= length)
				return -1;
			int n = Math.min(length - next, len);
			str.getChars(next, next + n, cbuf, off);
			next += n;
			return n;
	}


	public long skip(long ns) throws IOException {
			if (next >= length)
				return 0;
			// Bound skip by beginning and end of the source
			long n = Math.min(length - next, ns);
			n = Math.max(-next, n);
			next += n;
			return n;
	}


	public boolean ready() throws IOException {
			return true;
	}

	public boolean markSupported() {
		return true;
	}


	public void mark(int readAheadLimit) throws IOException {
		if (readAheadLimit < 0){
			throw new IllegalArgumentException("Read-ahead limit < 0");
		}
		mark = next;
	}


	public void reset() throws IOException {
			next = mark;
	}

	public void close() {

	}
}
