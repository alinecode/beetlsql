package org.beetl.sql.core.engine;

import java.io.IOException;

import org.beetl.core.Tag;

public class PageQueryTag extends Tag {

	@Override
	public void render() {
		Object o = ctx.getGlobal(PageQuery.pageFlag);
		if(o==PageQuery.pageObj){
			try {
				this.bw.writeString("count(1)");
			} catch (IOException e) {
				//不可能发生
				e.printStackTrace();
			}
			
		}else{
			this.doBodyRender();
			
		}
	}

}
