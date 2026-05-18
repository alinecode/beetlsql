package org.beetl.sql.jooq;

import javax.annotation.processing.*;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;
@SupportedAnnotationTypes("org.beetl.sql.jooq.JooqCodeGen")
public class JooqCodeGenProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			//简单向控制台输出消息
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Hello World!");
		}
		return true;
	}
}
