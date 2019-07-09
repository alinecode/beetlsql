package org.beetl.sql.core.engine;

import org.beetl.core.tag.Tag;

import java.io.IOException;

public class PageQueryTag extends Tag {

	@Override
	public void render() {
		Object o = ctx.getGlobal(PageQuery.pageFlag);
		if (o == PageQuery.pageObj) {
			try {
				this.bw.writeString("count(1)");
			} catch (IOException e) {
				//不可能发生
				e.printStackTrace();
			}

		} else {
			this.doBodyRender();

		}
	}

}
