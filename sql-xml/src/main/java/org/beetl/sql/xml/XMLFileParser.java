package org.beetl.sql.xml;

import org.apache.commons.lang3.StringEscapeUtils;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.loader.SQLFileParser;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.w3c.dom.Node.ELEMENT_NODE;

/**
 * 解析xml文件。解析过程中，也对xml做某些调整以适应beetl的html tag
 */
public class XMLFileParser implements SQLFileParser {
	String modelName;
	Reader reader;
	List<SQLSource> list = new ArrayList<>();
	int i = 0;

	public XMLFileParser(String modelName, Reader reader) {
		this.modelName = modelName;
		this.reader = reader;
		try {
			init();
		}catch (BeetlSQLException ex){
			throw ex;
		}
		catch (Exception e) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "解析xml错 " + modelName + " error:" + e.getMessage(),
				e);
		}

	}

	private void init() throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//创建DocumentBuilder对象
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(new InputSource(reader));
		{
			//获取所有book节点的集合
			NodeList sqlList = document.getElementsByTagName("sql");
			int len = sqlList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = sqlList.item(i);
				String id = node.getAttributes().item(0).getTextContent();
				StringBuilder sb = new StringBuilder();
				parseSqlNode(node, sb);
				SQLSource sqlSource = new SQLSource();
				sqlSource.id = SqlId.of(modelName, id);
				sqlSource.template = sb.toString();
				//XML解析起无法访问xml node所在源文件的行
				sqlSource.setLine(1);
				list.add(sqlSource);
			}
		}


		{
			NodeList resultMapList = document.getElementsByTagName("resultMap");
			int len = resultMapList.getLength();

			for (int i = 0; i < len; i++) {
				Node node = resultMapList.item(i);
				String id = node.getAttributes().item(0).getTextContent();
				Map<String, Object> map = new HashMap<>();
				parseMapNode(id,node, map);
				XMLResultMapSource sqlSource = new XMLResultMapSource();
				sqlSource.id = SqlId.of(modelName, id);
				sqlSource.template = null;
				sqlSource.mapConfig = map;
				sqlSource.setLine(1);
				list.add(sqlSource);
			}

		}


	}

	@Override
	public SQLSource next() throws IOException {
		if (list.size() == i) {
			return null;
		}
		SQLSource sqlSource = list.get(i);
		i++;
		return sqlSource;
	}


	protected void parseMapNode(String id,Node parent, Map<String, Object> map) {
		NodeList sqlList = parent.getChildNodes();
		for (int i = 0; i < sqlList.getLength(); i++) {
			Node node = sqlList.item(i);
			if(node.getNodeType()!=ELEMENT_NODE){
				continue;
			}
			if (node.getNodeName().equals("result")) {
				String property = node.getAttributes().getNamedItem("property").getTextContent();
				String column = node.getAttributes().getNamedItem("column").getTextContent();
				map.put(property,column);
			}else if(node.getNodeName().equals("association")||node.getNodeName().equals("collection")){
				String property = node.getAttributes().getNamedItem("property").getTextContent();
				Map<String, Object> childMap = new HashMap<>();
				parseMapNode(id,node, childMap);
				map.put(property,childMap);
			}else {
				throw new BeetlSQLException(BeetlSQLException.MAPPER_ERROR,"不支持的xml节点'"+node.getNodeName()+"' ,位于 "+modelName+"."+id);
			}



		}
	}

	protected void parseSqlNode(Node parent, StringBuilder sb) {
		NodeList sqlList = parent.getChildNodes();
		for (int i = 0; i < sqlList.getLength(); i++) {
			Node node = sqlList.item(i);
			if (node.getNodeType() == Node.COMMENT_NODE) {
				continue;
			} else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
				sb.append(node.getTextContent());
			} else if (node.getNodeType() == ELEMENT_NODE) {
				genNodeContent(node, sb);
			} else if (node.getNodeType() == Node.TEXT_NODE) {
				String str = node.getTextContent();
				str = StringEscapeUtils.unescapeXml(str);
				sb.append(str);
			} else {
				continue;
			}

		}
	}

	private void genNodeContent(Node node, StringBuilder sb) {
		String nodeName = "b:" + node.getNodeName();
		//		String nodeName =node.getNodeName();
		sb.append("<").append(nodeName);
		NamedNodeMap namedNodeMap = node.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node attrNode = namedNodeMap.item(i);
			if (attrNode.getNodeType() == Node.ATTRIBUTE_NODE) {
				Attr attr = (Attr) attrNode;
				String name = attr.getName();
				String value = attr.getValue();

				if (!XMLBeetlSQL.holderSet.contains(node.getNodeName() + "_" + name)) {
					sb.append(" ").append(attr.getName()).append("=").append('"').append(value).append('"');
				} else {
					//beetl表达式，比如<if test="a==1" > 翻译成 <s:if test="#{a==1}"
					value = parseValue(value);
					sb.append(" ").append(attr.getName()).append("=").append("\"#{").append(value).append("}\"");

				}

			}

		}
		if (!node.hasChildNodes()) {
			sb.append("/>");
			return;
		}
		sb.append(">");
		parseSqlNode(node, sb);
		sb.append("</").append(nodeName).append(">");
	}

	/**
	 * xml中不支持&&，因此需要使用and ，在这里把and替换成beetl识别的||
	 * @param value
	 */
	private String parseValue(String value) {
		//TODO,优化，
		value = value.replace(" and ", "&&").replace(" or ", "||");
		return value;
	}
}



