package org.beetl.sql.ext.gen;

public class GenConfig {
	//基类，默认就是Object
	public String baseClass;
	//格式控制，4个隔空
	public int spaceCount = 4;
	// double 类型采用BigDecimal
	private boolean preferBigDecimal = false ;
	//对于数字，优先使用封装类型
//	private boolean preferPrimitive = false ;
	
	private boolean display = false ;
	
	public String space = "    ";
	
	public  GenConfig setBaseClass(String baseClass){
		this.baseClass = baseClass;
		return this ;
	}
	public GenConfig setSpace(int count){
		this.spaceCount = count ;
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<count;i++){
			sb.append(" ");
		}
		space = sb.toString();
		return this;
	}
	
	public GenConfig preferBigDecimal(boolean prefer){
		this.preferBigDecimal = prefer;
		return this;
	}
	
	public GenConfig preferPrimitive(boolean primitive){
		this.preferBigDecimal = primitive;
		return this;
	}
	public String getBaseClass() {
		return baseClass;
	}
	public int getSpaceCount() {
		return spaceCount;
	}
	public boolean isPreferBigDecimal() {
		return preferBigDecimal;
	}

	
	public String getSpace(){
		return space;
	}
	public boolean isDisplay() {
		return display;
	}
	public GenConfig setDisplay(boolean display) {
		this.display = display;
		return this;
	}
	
	
	
}
