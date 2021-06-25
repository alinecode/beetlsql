package org.beetl.sql.starter;

import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
@Import({BeetlSqlBeanRegister.class})
public class BeetlSqlStater {
	
	@Autowired(required=false)
	SQLManagerCustomize cust;
	
	@Autowired()
    ApplicationContext context;
	
	@Autowired()
    Environment env;

	
	
	@PostConstruct
	public void init() {
		if(cust==null) {
			return ;
		}

		BeetlSqlConfig beetlSqlConfig = new BeetlSqlConfig(env);

		beetlSqlConfig.configs.entrySet().forEach(entry->{
			String name = entry.getKey();
			SQLManager sqlManager = context.getBean(name,SQLManager.class);
			cust.customize(name,sqlManager);
		});

	}




}
