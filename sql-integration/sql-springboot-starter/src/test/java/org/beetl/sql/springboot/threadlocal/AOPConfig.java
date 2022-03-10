package org.beetl.sql.springboot.threadlocal;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.beetl.sql.core.ThreadLocalSQLManager;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class AOPConfig {
    @Around("within(@org.springframework.stereotype.Service *) && @annotation(use)")
    public Object functionAccessCheck(final ProceedingJoinPoint pjp,Use use) throws Throwable {
        String old = ThreadLocalSQLManager.locals.get();
        try{
            Object[] args = pjp.getArgs();
            String targetSqlManager = use.value();
            ThreadLocalSQLManager.locals.set(targetSqlManager);
            Object o = pjp.proceed();
            return o;
        }finally {
            ThreadLocalSQLManager.locals.set(old);
        }

    }
}