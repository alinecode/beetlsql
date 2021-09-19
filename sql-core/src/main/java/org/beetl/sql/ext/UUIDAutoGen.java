package org.beetl.sql.ext;

import org.beetl.sql.core.IDAutoGen;

import java.util.UUID;

/**
 * uuid,基于版本uuid 版本3
 * <pre>{code
 * @AssingId("uuid")
 * String id;
 * }</pre>
 *
 * 如果想使用压缩版本的，可以参考 CompressedUUIDAutoGen
 *
 *
 *
 * 必须调用{@link org.beetl.sql.core.SQLManager#addIdAutoGen} 来使用
 * @see org.beetl.sql.annotation.entity.AssignID
 * @author xiandafu
 */
public class UUIDAutoGen implements IDAutoGen<String> {

	public UUIDAutoGen() {

	}

	@Override
	public String nextID(String params) {
		return UUID.randomUUID().toString();
	}

}
