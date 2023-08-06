package org.beetl.sql.firewall;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.PluginExtConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FireWallConfig implements PluginExtConfig {
	FireWall  fireWall;
	public FireWallConfig(FireWall fireWall){
		this.fireWall = fireWall;
	}
	@Override
	public void config(SQLManager sqlManager) {
		FireWallInterceptor fireWallInterceptor = new FireWallInterceptor(fireWall);
		Interceptor[] olds = sqlManager.getInters() ;
		if(olds==null||olds.length==0){
			sqlManager.setInters(new Interceptor[]{fireWallInterceptor});
			return ;
		}

		List list = new ArrayList<>(Arrays.asList(olds));
		list.add(fireWallInterceptor);
		sqlManager.setInters((Interceptor[]) list.toArray(new Interceptor[0]));
	}

}
