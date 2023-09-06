package org.beetl.sql.test.jpa;

import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.beetl.sql.mapper.builder.MapperMethodParser;
import org.beetl.sql.mapper.springdata.SpringDataBuilder;

public class JPAMapperMethodParser extends MapperMethodParser {

	@Override

	public MapperInvoke parse() {
		return super.parse();
	}
	//没有任何自注解，默认为spring data 风格
	@Override
	protected MapperInvoke others(){
		SpringDataBuilder springDataBuilder = new SpringDataBuilder();
		MapperInvoke mapperInvoke = springDataBuilder.parse(this.mapperClass,this.method);
		return mapperInvoke;
	}




	protected MapperExtBuilder findExtBuilder() {
		return null;
	}
}
