package org.beetl.sql.starter;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
@Import({BeetlSqlBeanRegister.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class BeetlSqlStater4 {


}
