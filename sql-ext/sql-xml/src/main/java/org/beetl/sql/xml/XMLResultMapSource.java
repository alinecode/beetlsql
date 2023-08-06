package org.beetl.sql.xml;

import org.beetl.sql.core.SQLSource;
import java.util.Map;

/**
 * 解析xml时候，碰到resultMap的xml节点，认为是映射配置文件，单独存放到 ResultMapSource
 */
public  class XMLResultMapSource extends SQLSource {
	public Map<String,Object> mapConfig ;
}
