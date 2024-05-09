package org.beetl.sql.jmh.jpa;

import lombok.Data;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Data
public class SpringBoot {
    SpringService service = null;
    public void init(){
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
        service  =  ctx.getBean(SpringService.class);

    }

}
