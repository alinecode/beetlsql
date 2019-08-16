package org.beetl.sql.core.engine;

import org.beetl.core.tag.Tag;

import java.io.IOException;

public class PageQueryIgnoreTag extends Tag {

	@Override
	public void render() {
		Object o = ctx.getGlobal(PageQuery.pageFlag);
		if(o!=null &&o==Boolean.TRUE){
			try {
				this.bw.writeString(" ");
			} catch (IOException e) {
				//不可能发生
				e.printStackTrace();
			}

		} else {
			this.doBodyRender();

		}
	}

}