package org.beetl.sql.jooq;

import org.jooq.AttachableQueryPart;
import org.jooq.DSLContext;

@FunctionalInterface
public  interface CtxQueryFunction {
	 AttachableQueryPart execute(DSLContext create);
}
