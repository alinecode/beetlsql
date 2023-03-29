package org.beetl.sql.xml;

import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLLoader {
	public static void main(String[] args) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			//创建DocumentBuilder对象
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(new ErrorHandler() {
			@Override
			public void warning(SAXParseException e) throws SAXException {
				System.out.println(e);
			}

			@Override
			public void error(SAXParseException e) throws SAXException {
				System.out.println(e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				System.out.println(e);
			}
		});
			//通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
			Document document = db.parse(XMLLoader.class.getResourceAsStream("/sql/user.xml"));

			//获取所有book节点的集合
			NodeList sqlList = document.getElementsByTagName("sql");
			int len = sqlList.getLength();
			for(int i=0;i<len;i++){
				Node node = sqlList.item(i);
				StringBuilder sb = new StringBuilder();
				parseSqlNode(node,sb);
				System.out.println(sb);
			}


	}
	private static void parseSqlNode(Node parent,StringBuilder sb){
		NodeList sqlList = parent.getChildNodes();
		for(int i=0;i<sqlList.getLength();i++){
			Node node = sqlList.item(i);
			if(node.getNodeType()==Node.COMMENT_NODE){
				continue;
			}else if(node.getNodeType()==Node.CDATA_SECTION_NODE){
				sb.append(node.getTextContent());
				continue;
			}else if(node.getNodeType()==Node.ELEMENT_NODE){
				genNodeContent(node,sb);
			}else if(node.getNodeType()==Node.TEXT_NODE){
				sb.append(node.getTextContent());

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
