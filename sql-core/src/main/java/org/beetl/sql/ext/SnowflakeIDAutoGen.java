package org.beetl.sql.ext;

import org.beetl.sql.core.IDAutoGen;

import java.util.Map;

/**
 * 一个简单雪花算法示例，注意，不要用它在集群环境，因为他的workId和datacenterId都是0
 * ，不具备分布式唯一性
 * <pre>{code
 * @AssingId("simple")
 * Long id;
 * }</pre>
 *
 * 必须调用{@link org.beetl.sql.core.SQLManager#addIdAutoGen} 来使用
 * @see org.beetl.sql.annotation.entity.AssignID
 * @author lijiazhi
 */
public class SnowflakeIDAutoGen implements IDAutoGen<Long> {

	SnowflakeIDWorker defaultWork = null;
	Map<String, SnowflakeIDWorker> map = null;

	public SnowflakeIDAutoGen() {
		this(0,0);
	}

	public SnowflakeIDAutoGen(long workId,long datacenterId) {
		defaultWork = new SnowflakeIDWorker(workId, datacenterId);
	}

	public SnowflakeIDAutoGen(Map<String, SnowflakeIDWorker> map) {
		this.map = map;

	}

	//Override
	@Override
    public Long nextID(String params) {
		if (params == null || params.length() == 0) {
			return defaultWork.nextId();
		} else {
			SnowflakeIDWorker worker = map.get(params);
			if (worker == null) {
				throw new NullPointerException("params " + params + " can not found id worker");
			}
			return worker.nextId();
		}
	}

}
