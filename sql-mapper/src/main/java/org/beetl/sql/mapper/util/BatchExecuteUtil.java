package org.beetl.sql.mapper.util;

import org.beetl.sql.clazz.kit.ListUtil;

import java.util.List;

/**
 * 分批处理工具类
 */
public class BatchExecuteUtil {
	public static <T> int[]  executeBatchList(List<T> list, int size,BatchExecuteAction batchExecuteAction) {
		List<List<T>> groupList = ListUtil.partition(list,size);
		int[] ret = new int[list.size()];
		for(int i=0;i<groupList.size();i++){
			int[] subRet = batchExecuteAction.execute(list);
			System.arraycopy(subRet,0,ret,i*size,subRet.length);
		}
		return ret;
	}
}
