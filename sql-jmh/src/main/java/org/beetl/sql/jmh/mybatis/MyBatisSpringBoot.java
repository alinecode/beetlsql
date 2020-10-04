package org.beetl.sql.jmh.mybatis;

import lombok.Data;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Data
public class MyBatisSpringBoot {
    MyBatisSpringService service = null;
    public void init(){
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(MyBatisAppConfig.class);
        ctx.refresh();
        service  =  ctx.getBean(MyBatisSpringService.class);

    }

}
