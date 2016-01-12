package org.beetl.sql.core.kit;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * JdbcKit.
 * 
 * @author zhoupan
 */
public class JdbcKit {

	/**
	 * JdbcConfig.
	 */
	public static class JdbcConfig {

		/** The id. */
		private String id;
		/** The driver. */
		private String driver;

		/** The url. */
		private String url;

		/** The user. */
		private String user;

		/** The password. */
		private String password;

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * Sets the id.
		 *
		 * @param id
		 *            the id
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * Gets the driver.
		 *
		 * @return the driver
		 */
		public String getDriver() {
			return driver;
		}

		/**
		 * Sets the driver.
		 *
		 * @param driver
		 *            the driver
		 */
		public void setDriver(String driver) {
			this.driver = driver;
		}

		/**
		 * Gets the url.
		 *
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Sets the url.
		 *
		 * @param url
		 *            the url
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * Gets the user.
		 *
		 * @return the user
		 */
		public String getUser() {
			return user;
		}

		/**
		 * Sets the user.
		 *
		 * @param user
		 *            the user
		 */
		public void setUser(String user) {
			this.user = user;
		}

		/**
		 * Gets the password.
		 *
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * Sets the password.
		 *
		 * @param password
		 *            the password
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((driver == null) ? 0 : driver.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((password == null) ? 0 : password.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JdbcConfig other = (JdbcConfig) obj;
			if (driver == null) {
				if (other.driver != null)
					return false;
			} else if (!driver.equals(other.driver))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (password == null) {
				if (other.password != null)
					return false;
			} else if (!password.equals(other.password))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

		/**
		 * Validate.
		 */
		public void validate() {
			if (this.id == null || this.driver == null || this.url == null || this.user == null
					|| this.password == null) {
				throw new RuntimeException("id/driver/user/password not allow null.");
			}
		}
	}

	/**
	 * JdbcConfigManager.
	 */
	public static interface JdbcConfigManager {

		/** The Constant DRIVER. */
		public static final String DRIVER = "jdbc.driver";

		/** The Constant URL. */
		public static final String URL = "jdbc.url";

		/** The Constant USER. */
		public static final String USER = "jdbc.user";

		/** The Constant PASSWORD. */
		public static final String PASSWORD = "jdbc.password";

		/**
		 * Gets the jdbc config.
		 *
		 * @param id
		 *            the id
		 * @return the jdbc config
		 */
		public JdbcConfig getJdbcConfig(String id);

		/**
		 * Put jdbc config.
		 *
		 * @param id
		 *            the id
		 * @param jdbcConfig
		 *            the jdbc config
		 */
		public void putJdbcConfig(String id, JdbcConfig jdbcConfig);

		/**
		 * Gets the props.
		 *
		 * @return the props
		 */
		public Properties getProps();
	}

	/**
	 * The Class JdbcConfigDataSource.
	 */
	public static class JdbcConfigDataSource implements javax.sql.DataSource {

		/** The jdbc config. */
		JdbcConfig jdbcConfig;

		/**
		 * The Constructor.
		 *
		 * @param jdbcConfig
		 *            the jdbc config
		 */
		public JdbcConfigDataSource(JdbcConfig jdbcConfig) {
			super();
			this.jdbcConfig = jdbcConfig;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.sql.CommonDataSource#getLogWriter()
		 */
		public PrintWriter getLogWriter() throws SQLException {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
		 */
		public void setLogWriter(PrintWriter out) throws SQLException {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.sql.CommonDataSource#setLoginTimeout(int)
		 */
		public void setLoginTimeout(int seconds) throws SQLException {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.sql.CommonDataSource#getLoginTimeout()
		 */
		public int getLoginTimeout() throws SQLException {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.sql.Wrapper#unwrap(java.lang.Class)
		 */
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
		 */
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.sql.DataSource#getConnection()
		 */
		public Connection getConnection() throws SQLException {
			Connection conn = null;
			try {
				conn = DriverManager.getConnection(this.jdbcConfig.getUrl(), this.jdbcConfig.getUser(),
						this.jdbcConfig.getPassword());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return conn;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.sql.DataSource#getConnection(java.lang.String,
		 * java.lang.String)
		 */
		public Connection getConnection(String username, String password) throws SQLException {
			return this.getConnection();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.sql.CommonDataSource#getParentLogger()
		 */
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}

	}

	/**
	 * The Class JdbcSupport.
	 */
	public static abstract class JdbcSupport {

		/** The jdbc config manager. */
		protected JdbcConfigManager jdbcConfigManager;

		/**
		 * The Constructor.
		 *
		 * @param jdbcConfigManager
		 *            the jdbc config manager
		 */
		public JdbcSupport(JdbcConfigManager jdbcConfigManager) {
			super();
			this.jdbcConfigManager = jdbcConfigManager;
		}

		/** The data sources. */
		Map<String, DataSource> dataSources = new java.util.concurrent.ConcurrentHashMap<String, DataSource>();

		/**
		 * Gets the data source.
		 *
		 * @param jdbcConfig
		 *            the jdbc config
		 * @return the data source
		 */
		public DataSource getDataSource(JdbcConfig jdbcConfig) {
			jdbcConfig.validate();
			if (dataSources.containsKey(jdbcConfig.getId())) {
				return dataSources.get(jdbcConfig.getId());
			}
			JdbcConfig existed = this.jdbcConfigManager.getJdbcConfig(jdbcConfig.getId());
			if (existed == null) {
				this.jdbcConfigManager.putJdbcConfig(jdbcConfig.getId(), jdbcConfig);
			}
			DataSource ds = this.buildDataSource(jdbcConfig);
			this.dataSources.put(jdbcConfig.getId(), ds);
			return ds;
		}

		/**
		 * Gets the data source.
		 *
		 * @param id
		 *            the id
		 * @return the data source
		 */
		public DataSource getDataSource(String id) {
			CommonKit.throwIfNull(id, "id of dataSource not allow null");
			JdbcConfig jdbcConfig = this.jdbcConfigManager.getJdbcConfig(id);
			CommonKit.throwIfNull(jdbcConfig, "jdbc config with id(" + id + ") not found.");
			return getDataSource(jdbcConfig);
		}

		/**
		 * Builds the data source.
		 *
		 * @param jdbcConfig
		 *            the jdbc config
		 * @return the data source
		 */
		public abstract DataSource buildDataSource(JdbcConfig jdbcConfig);

	}

	/**
	 * The Class DefaultJdbcSupport.
	 */
	public static class DefaultJdbcSupport extends JdbcSupport {

		/**
		 * The Constructor.
		 *
		 * @param jdbcConfigManager
		 *            the jdbc config manager
		 */
		public DefaultJdbcSupport(JdbcConfigManager jdbcConfigManager) {
			super(jdbcConfigManager);
		}

		/**
		 * Builds the data source.
		 *
		 * @param jdbcConfig
		 *            the jdbc config
		 * @return the data source
		 */
		public DataSource buildDataSource(JdbcConfig jdbcConfig) {
			return new JdbcConfigDataSource(jdbcConfig);
		}
	}

	/**
	 * DefaultJdbcConfigManager.
	 */
	public static class DefaultJdbcConfigManager extends HashMap<String, JdbcConfig>implements JdbcConfigManager {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
		/** The props. */
		Properties props;

		/**
		 * The Constructor.
		 *
		 * @param props
		 *            the props
		 */
		public DefaultJdbcConfigManager(Properties props) {
			super();
			this.props = props;
		}

		/**
		 * Gets the props.
		 *
		 * @return the props
		 */
		public Properties getProps() {
			return props;
		}

		/**
		 * Gets the jdbc config.
		 *
		 * @param id
		 *            the name
		 * @return the jdbc config
		 */
		public JdbcConfig getJdbcConfig(String id) {
			if (this.containsKey(id)) {
				return this.get(id);
			}
			JdbcConfig config = new JdbcConfig();
			config.setId(id);
			config.setDriver(CommonKit.getPropertyWithPrefix(props, id, JdbcConfigManager.DRIVER));
			config.setUrl(CommonKit.getPropertyWithPrefix(props, id, JdbcConfigManager.URL));
			config.setUser(CommonKit.getPropertyWithPrefix(props, id, JdbcConfigManager.USER));
			config.setPassword(CommonKit.getPropertyWithPrefix(props, id, JdbcConfigManager.PASSWORD));
			this.put(id, config);
			return config;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.beetl.sql.core.kit.JdbcKit.JdbcConfigManager#putJdbcConfig(java.
		 * lang.String, org.beetl.sql.core.kit.JdbcKit.JdbcConfig)
		 */
		public void putJdbcConfig(String id, JdbcConfig jdbcConfig) {
			this.put(id, jdbcConfig);
		}

		/**
		 * From resource.
		 *
		 * @param resourceName
		 *            the resource name
		 * @return the default jdbc config manager
		 */
		public static DefaultJdbcConfigManager fromResource(String resourceName) {
			return new DefaultJdbcConfigManager(CommonKit.readPropertiesFromResource(resourceName));
		}

	}
}
