package org.beetl.sql.ext;

import org.beetl.sql.core.IDAutoGen;

import java.util.UUID;

/**
 * uuid
 * <pre>{code
 * @AssingId("uuid")
 * String id;
 * }</pre>
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
