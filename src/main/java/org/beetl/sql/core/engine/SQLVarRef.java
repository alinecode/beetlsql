package org.beetl.sql.core.engine;

import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.misc.PrimitiveArrayUtil;
import org.beetl.core.om.MethodInvoker;
import org.beetl.core.om.ObjectAA;
import org.beetl.core.om.ObjectUtil;
import org.beetl.core.statement.VarRef;

public class SQLVarRef extends VarRef {
	String attr;

	public SQLVarRef(VarRef ref) {
		super(ref.attributes, ref.hasSafe, ref.safe, ref.token, ref.token);
		this.varIndex = ref.varIndex;
		attr = getAttrNameIfRoot(ref.token.text);

	}

	public Object evaluate(Context ctx) {

		Object value = ctx.vars[varIndex];
		if (value == Context.NOT_EXIST_OBJECT) {
			// check root
			Object o = ctx.getGlobal("_root");
			if (o == null) {
				return super.evaluate(ctx);
			} else {
				try {
					if(this.hasSafe&&!this.hasAttr(o, attr)){
						return safe == null ? null : safe.evaluate(ctx);
					}
					Object realValue = ObjectAA.defaultObjectAA().value(o, attr);
					ctx.vars[varIndex] = realValue;

				} catch (Exception e) {

					BeetlException ex = new BeetlException(BeetlException.VAR_NOT_DEFINED, e.getMessage());
					ex.pushToken(this.token);
					throw ex;
				}
				return super.evaluate(ctx);

			}
		}
		return super.evaluate(ctx);
	}

	private String getAttrNameIfRoot(String name) {
		// todo []
		int index = name.indexOf('.');
		if (index != -1) {
			return name.substring(0, index);
		} else {
			return name;
		}
	}

	private boolean hasAttr(Object o, String attr) {

		if (o instanceof Map) {
			return ((Map) o).containsKey(attr);
		} else if (o instanceof List) {
			return true;

		} else if (o.getClass().isArray()) {
			return true;

		}

		else {

			Class c = o.getClass();
			MethodInvoker invoker = ObjectUtil.getInvokder(c, (String) attr);
			if (invoker != null) {
				return true;
			} else {
				return false;
			}

		}
	}
}
