package org.beetl.sql.xml;

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
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class XMLFileParser implements SQLFileParser {
	String modelName;
	Reader reader;
	List<SQLSource> list = new ArrayList<>();
	int i = 0;
	public XMLFileParser(String modelName, Reader reader){
		this.modelName = modelName;
		this.reader =reader;
		try {
			init();
		} catch (Exception e) {
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL,"解析xml错 "+modelName+" error:"+e.getMessage(),e);
		}

	}

	private void init() throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//创建DocumentBuilder对象
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(new InputSource(reader));
		//获取所有book节点的集合
		NodeList sqlList = document.getElementsByTagName("sql");
		int len = sqlList.getLength();
		for(int i=0;i<len;i++){
			Node node = sqlList.item(i);
			String id  =node.getAttributes().item(0).getTextContent();
			StringBuilder sb = new StringBuilder();
			parseSqlNode(node,sb);
			SQLSource sqlSource = new SQLSource();
			sqlSource.id = SqlId.of(modelName,id);
			sqlSource.template = sb.toString();
			list.add(sqlSource);
		}

	}
	@Override
	public SQLSource next() throws IOException {
		if(list.size()==i){
			return null ;
		}
		SQLSource sqlSource = list.get(i);
		i++;
		return sqlSource;
	}

	private static void parseSqlNode(Node parent, StringBuilder sb){
		NodeList sqlList = parent.getChildNodes();
		for(int i=0;i<sqlList.getLength();i++){
			Node node = sqlList.item(i);
			if(node.getNodeType()==Node.COMMENT_NODE){
				continue;
			}else if(node.getNodeType()==Node.CDATA_SECTION_NODE){
				sb.append(node.getTextContent());
			}else if(node.getNodeType()==Node.ELEMENT_NODE){
				genNodeContent(node,sb);
			}else if(node.getNodeType()==Node.TEXT_NODE){
				sb.append(node.getTextContent());
			}else{
				continue;
			}

		}
	}

	private static void genNodeContent(Node node,StringBuilder sb){
		String nodeName ="b:"+node.getNodeName();
		sb.append("<").append(nodeName);
		NamedNodeMap namedNodeMap = node.getAttributes();
		for(int i=0;i<namedNodeMap.getLength();i++){
			Node attrNode = namedNodeMap.item(i);
			if(attrNode.getNodeType()==Node.ATTRIBUTE_NODE) {
				Attr attr = (Attr) attrNode;
				sb.append(" ").append(attr.getName()).append("=").append('"').append(attr.getValue()).append('"');
			}

		}
		if(!node.hasChildNodes()){
			sb.append("/>");
			return ;
		}
		sb.append(">");
		parseSqlNode(node,sb);
		sb.append("</").append(nodeName).append(">");
	}
}
