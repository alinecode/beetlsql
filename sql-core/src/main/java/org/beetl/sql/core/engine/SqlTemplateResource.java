package org.beetl.sql.core.engine;

import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLLoader;

import java.io.IOException;
import java.io.Reader;

public class SqlTemplateResource extends Resource<SqlId> {

	int line = 0;

	public SqlTemplateResource(SqlId id, ResourceLoader loader) {
		super(id, loader);

	}

	@Override
	public Reader openReader() {
		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();
		SQLSource newResource = loader.querySQL(id);
		this.line = newResource.getLine();
		return new NoneBlockStringReader(newResource.getTemplate());
	}

	@Override
	public boolean isModified() {

		StringSqlTemplateLoader l = (StringSqlTemplateLoader) this.resourceLoader;
		SQLLoader loader = l.getSqlLLoader();
		return loader.isModified(id);

	}

	public int getLine() {
		return this.line;
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

	@Override
	public int read() throws IOException {

		if (next >= length) {
			return -1;
		}
		return str.charAt(next++);
	}

	@Override
	public int read(char cbuf[], int off, int len) throws IOException {
			if ((off < 0) || (off > cbuf.length) || (len < 0) ||
					((off + len) > cbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}
			if (next >= length) {
				return -1;
			}
			int n = Math.min(length - next, len);
			str.getChars(next, next + n, cbuf, off);
			next += n;
			return n;
	}


	@Override
	public long skip(long ns) throws IOException {
			if (next >= length) {
				return 0;
			}
			// Bound skip by beginning and end of the source
			long n = Math.min(length - next, ns);
			n = Math.max(-next, n);
			next += n;
			return n;
	}


	@Override
	public boolean ready() throws IOException {
			return true;
	}

	@Override
	public boolean markSupported() {
		return true;
	}


	@Override
	public void mark(int readAheadLimit) throws IOException {
		if (readAheadLimit < 0){
			throw new IllegalArgumentException("Read-ahead limit < 0");
		}
		mark = next;
	}


	@Override
	public void reset() throws IOException {
			next = mark;
	}

	@Override
	public void close() {

	}
}
