package org.beetl.sql.mapper.util;

import org.beetl.sql.clazz.kit.ListUtil;

import java.util.List;

/**
 * 分批处理工具类
 */
public class BatchExecuteUtil {
	public static <T> int[]  executeBatchList(List<T> list, int size,BatchExecuteAction batchExecuteAction) {
		List<List<T>> groupList = ListUtil.partition(list,size);
		if(groupList.size()==1){
			//如果只有一组，直接执行
			return batchExecuteAction.execute(groupList.get(0));
		}
		int[] ret = new int[list.size()];
		for(int i=0;i<groupList.size();i++){
			int[] subRet = batchExecuteAction.execute(groupList.get(i));
			System.arraycopy(subRet,0,ret,i*size,subRet.length);
		}
		return ret;
	}
}
