package org.beetl.sql.xml;

import org.beetl.core.tag.HTMLTagSupportWrapper;
import org.beetl.core.tag.TagFactory;
import org.beetl.sql.clazz.kit.BeetlSQLException;

/**
 * 同父类，但不会寻找xml 文件的tag实现，只找Tag类的子类实现
 */
public class XMLTagSupportWrapper extends HTMLTagSupportWrapper {
	@Override
	public void render() {
		if (args.length == 0 || args.length > 2) {
			throw new RuntimeException("参数错误，期望child,Map .....");
		}
		//标签名称
		String child = (String) args[0];
		// 首先查找 已经注册的Tag
		TagFactory tagFactory = null;
		String functionTagName = child.replace(':', '.');
		tagFactory = this.gt.getTagFactory(functionTagName);
		if (tagFactory == null) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL,"未能扎到"+child+"标签的实现类");

		}
		callTag(tagFactory);

	}
}
