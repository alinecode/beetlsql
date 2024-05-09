package org.beetl.sql.jmh.mybatis;


import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.beetl.sql.jmh.base.DataSourceHelper;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

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
