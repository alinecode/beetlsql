package org.beetl.sql.core.loader;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AbsolutePathLoader extends PathLoader {
	public AbsolutePathLoader(String root, String charset) {
		super(root,charset);

	}

	public AbsolutePathLoader(String root) {
		super(root);

	}

	public AbsolutePathLoader() {
		this("sql");
	}

	@Override
	protected URL getFile(String filePath) {
		File file = new File(filePath);
		if(!file.exists()){
			return  null;
		}
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(filePath+" to url");
		}
	}

	@Override
	protected SQLFileParser getParser(String modelName, BufferedReader br) throws IOException {
		MarkdownParser parser = new MarkdownParser(modelName, br);
		return parser;
	}
}
