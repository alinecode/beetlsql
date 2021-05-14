package org.beetl.sql.clazz.kit;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 在beetlsql中用于获取最佳的类加载器
 */
public class ClassLoaderKit {
	List<ClassLoader> otherLoaders = new ArrayList<>();

	public ClassLoaderKit(ClassLoader first) {
		otherLoaders.add(first);
	}

	public ClassLoaderKit() {
		ClassLoader first = getDefaultLoader();
		otherLoaders.add(first);
	}


	public ClassLoader getPreferredLoader() {
		return otherLoaders.get(0);
	}

	public Class loadClass(String className) {
		for (int i = 0; i < otherLoaders.size(); i++) {
			ClassLoader loader = (ClassLoader) otherLoaders.get(i);
			try {
				Class c = loader.loadClass(className);
			} catch (Exception ex) {

			}

		}
		return null;
	}

	public InputStream loadResource(String resource) {
		for (int i = 0; i < otherLoaders.size(); i++) {
			ClassLoader loader = (ClassLoader) otherLoaders.get(i);
			InputStream inputStream = loader.getResourceAsStream(resource);
			if (inputStream != null) {
				return inputStream;
			}
		}
		return null;
	}

	public URL loadResourceAsURL(String resource) {
		for (int i = 0; i < otherLoaders.size(); i++) {
			ClassLoader loader = (ClassLoader) otherLoaders.get(i);
			URL url = loader.getResource(resource);
			if (url != null) {
				return url;
			}
		}
		return null;
	}

	protected ClassLoader getDefaultLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			return loader;
		}
		//有点危险
		return ClassLoaderKit.class.getClassLoader();
	}
}
