package org.beetl.sql.test.jpa;

import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.mapper.internal.InsertAMI;

import java.util.Optional;

/**
 * 重新定制beetlsql mapper，高仿Spring Data
 * @param <T>
 * @param <ID>
 */
@JPASupport
public interface CrudRepository<T,ID> {
	@AutoMapper(JPASingleAMI.class)
	Optional<T> findById(ID primaryKey);
	@AutoMapper(InsertAMI.class)
	<S extends T> S save(S entity);
}
