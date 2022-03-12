package org.beetl.sql.springboot.threadlocal;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.beetl.sql.core.ThreadLocalSQLManager;
import org.springframework.context.annotation.Configuration;

/**
 * 为service提供一个切面
 */
@Aspect
@Configuration
public class AOPConfig {
    @Around("within(@org.springframework.stereotype.Service *)")
    public Object functionAccessCheck(final ProceedingJoinPoint pjp) throws Throwable {
		String old = ThreadLocalSQLManager.locals.get();
		MethodSignature sig = (MethodSignature)pjp.getSignature();
		Use use = sig.getMethod().getAnnotation(Use.class);
		if(use==null){
			//设置为默认
			ThreadLocalSQLManager.locals.set(null);
		}else{
			String targetSqlManager = use.value();
			ThreadLocalSQLManager.locals.set(targetSqlManager);
		}

        try{
            Object o = pjp.proceed();
            return o;
        }finally {
            ThreadLocalSQLManager.locals.set(old);
        }

    }
}