package org.beetl.sql.ext;

import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SQLManager;

@Plugin
public interface PluginExtConfig {
	void config(SQLManager sqlManager);
}

