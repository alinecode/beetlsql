package org.beetl.sql.mapper.ready;

import lombok.Data;
import org.beetl.sql.mapper.MapperInvoke;

/**
 *
 * @author xiandafu
 */
@Data
public abstract  class BaseSqlReadyMI extends MapperInvoke {
    String sql;
    Class targetType;

}
