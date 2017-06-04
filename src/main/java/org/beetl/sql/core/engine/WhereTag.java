package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.Map;

import org.beetl.sql.core.kit.StringKit;

public class WhereTag extends TrimTag{
	@Override
	public void render() {
		try {
			initTrimArgs(args);
			StringBuilder sb = buildTrimContent();
			this.ctx.byteWriter.writeString(sb.toString());
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	@Override
	protected void initTrimArgs(Object[] args) {
		if(args!=null && args.length>0){
			for (Object arg : args) {
				if (arg instanceof Map) {
					Map<String, Object> params = (Map<String, Object>) arg;
					if (params.containsKey(PREFIX)) {
						this.prefix = (String) params.get(PREFIX);
					}else{
						this.prefix = "WHERE";
					}
					if (params.containsKey(PREFIX_OVERRIDES)) {
						this.prefixOverrides = StringKit.split((String) params.get(PREFIX_OVERRIDES), SEPARATOR_CHAR);
					}else{
						this.prefixOverrides = new String[]{"AND ","OR "};
					}
				}
			}
		}else{
			this.prefix = "WHERE";
			this.prefixOverrides = new String[]{"AND ","OR "};
		}
	}
}
