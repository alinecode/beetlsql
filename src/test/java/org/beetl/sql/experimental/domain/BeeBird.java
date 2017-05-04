package org.beetl.sql.experimental.domain;

import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.mapper.internal.MyMapper;

/**
 * <pre>
 * 表结构请到这里下载:  http://git.oschina.net/iohao/beetlsql-experimental
 * dir/sql/bird.sql
 * </pre>
 * <p>
 * create time : 2017-04-27 18:29
 *
 * @author luoyizhu@gmail.com
 */
@Table(name = "tb_bird")
public class BeeBird {
    int id;
    /** 年龄 */
    Integer age;
    /** 名字 */
    String name;
    /** 描述 */
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BeeBird{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public interface Dao extends MyMapper<BeeBird> {

    }

    public interface BaseDao extends BaseMapper<BeeBird> {

    }
}
