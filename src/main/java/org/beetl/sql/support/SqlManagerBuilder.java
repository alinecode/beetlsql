package org.beetl.sql.support;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.core.db.SQLiteStyle;
import org.beetl.sql.core.kit.CommonKit;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * SqlManagerBuilder.
 * 
 * @author zhoupan
 */
public class SqlManagerBuilder {

	/** The db style. */
	private DBStyle dbStyle;

	/** The sql loader. */
	private SQLLoader sqlLoader;

	/** The cs. */
	private ConnectionSource cs;

	/** The nc. */
	private NameConversion nc;

	/** The interceptors. */
	private List<Interceptor> interceptors = new ArrayList<Interceptor>();

	/**
	 * Db style.
	 *
	 * @param dbStyle
	 *            the db style
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder dbStyle(DBStyle dbStyle) {
		this.dbStyle = dbStyle;
		return this;
	}

	/**
	 * Sql loader.
	 *
	 * @param sqlLoader
	 *            the sql loader
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder sqlLoader(SQLLoader sqlLoader) {
		this.sqlLoader = sqlLoader;
		return this;
	}

	/**
	 * Connection source.
	 *
	 * @param cs
	 *            the cs
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder connectionSource(ConnectionSource cs) {
		this.cs = cs;
		return this;
	}

	/**
	 * Connection source.
	 *
	 * @param master
	 *            the master
	 * @param slaves
	 *            the slaves
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder connectionSource(DataSource master, DataSource[] slaves) {
		this.cs = new DefaultConnectionSource(master, slaves);
		return this;
	}

	/**
	 * Connection source.
	 *
	 * @param master
	 *            the master
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder connectionSource(DataSource master) {
		this.cs = new DefaultConnectionSource(master, null);
		return this;
	}

	/**
	 * Name conversion.
	 *
	 * @param nc
	 *            the nc
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder NameConversion(NameConversion nc) {
		this.nc = nc;
		return this;
	}

	/**
	 * Interceptor.
	 *
	 * @param items
	 *            the items
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder interceptor(Interceptor... items) {
		if (items != null) {
			for (Interceptor inter : items) {
				interceptors.add(inter);
			}
		}
		return this;
	}

	/**
	 * Debug.
	 *
	 * @return the sql manager builder
	 */
	public SqlManagerBuilder debug() {
		this.interceptors.add(new DebugInterceptor());
		return this;
	}

	/**
	 * On build name conversion.
	 *
	 * @return the name conversion
	 */
	public NameConversion onBuildNameConversion() {
		return new UnderlinedNameConversion();
	}

	/**
	 * On resolve db style.
	 *
	 * @param dbMeta
	 *            the db meta
	 * @return the DB style
	 * @throws SQLException
	 *             the SQL exception
	 */
	public DBStyle onResolveDBStyle(DatabaseMetaData dbMeta) throws SQLException {
		String dbProductName = dbMeta.getDatabaseProductName();
		if (CommonKit.containsIgnoreCase(dbProductName, "mysql")) {
			return new MySqlStyle();
		}
		if (CommonKit.containsIgnoreCase(dbProductName, "oracle")) {
			return new OracleStyle();
		}
		if (CommonKit.containsIgnoreCase(dbProductName, "sqlite")) {
			return new SQLiteStyle();
		}
		if (CommonKit.containsIgnoreCase(dbProductName, "h2")) {
			return new H2Style();
		}
		CommonKit.throwThat(String.format("can't resolve dbStyle for productName=%s", dbProductName));
		return null;
	}

	/**
	 * On build sql loader.
	 *
	 * @return the SQL loader
	 */
	public SQLLoader onBuildSQLLoader() {
		return new ClasspathLoader("/sql");
	}

	/**
	 * Builds the.
	 *
	 * @return the SQL manager
	 */
	public SQLManager build() {
		if (this.nc == null) {
			this.nc = this.onBuildNameConversion();
		}
		if (this.sqlLoader == null) {
			this.sqlLoader = this.onBuildSQLLoader();
		}
		if (this.dbStyle == null) {
			// resolve dbStyle from metaData
			if (this.cs != null) {
				try {
					this.dbStyle = onResolveDBStyle(this.cs.getMaster().getMetaData());
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
		CommonKit.throwIfNull(this.dbStyle, "DbStyle required.");
		Interceptor[] inters = new Interceptor[this.interceptors.size()];
		this.interceptors.toArray(inters);
		return new SQLManager(dbStyle, sqlLoader, cs, nc, inters);
	}
}
