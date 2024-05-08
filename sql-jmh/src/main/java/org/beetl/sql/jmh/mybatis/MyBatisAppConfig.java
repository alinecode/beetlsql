package org.beetl.sql.jmh.mybatis;


import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.beetl.sql.jmh.DataSourceHelper;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@ComponentScan(basePackages = "org.beetl.sql.jmh.mybatis")
@Configuration
public class MyBatisAppConfig {
    @Bean
    public DataSource dataSource() {
        return DataSourceHelper.newDatasource();
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("org.beetl.sql.jmh.mybatis");
        return configurer;
    }

    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(DataSource dataSource) {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new ClassPathResource("mapper/user.xml"));
        return bean;

    }


}
