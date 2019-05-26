package org.beetl.sql.test;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.*;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;

@SqlResource("user")
public interface UserDao extends BaseDao {

    List<Long> getIds();

    @SqlProvider(provider = UserSqlProvider.class)
    User selectAll1( Integer id);

    @SqlProvider(provider = UserSqlProvider.class)
    @Sql()
    User selectAll2( Integer id);

    @SqlProvider(provider = UserSqlProvider.class,method="delete2")
    @Sql()
    int deleteUser(Integer id);

    void getIds3(PageQuery query);

    // void pageQuery(PageQuery<String> query);
    //
    int getCount(String name);

    User getOneUser();

    User findOne( @Param("id") Integer id);

    List<Map<String, Object>> getIdNames();

    @Sql(value = "select id from user")
    List<Long> getIds2();

    @Sql(value = "select id,name from user where id=?")
    Map<String, Long> getUserInfo(Long id);

    List getUsers(int hi, User user);

    @Sql("select * from user where name=? ")
    PageQuery<User> getUser4(int pageNumber, int pageSize, String name);

    void getUser5(PageQuery<User> query, String name);

    List<User> select(Map user);

    @SqlStatement(params = "name,id")
    public int updateUser(String name, int id);

    int deleteByUserIds(List<Integer> userIds);

    public int updateUser(List<User> users);

    public KeyHolder addOne(User user);

    @Sql(value = "truncate table test immediate", type = SqlStatementType.UPDATE)
    public void dropTable();

    public int batchUpdate(List<User> users);

    // default User queryUserId(Integer id) {
    // return createLambdaQuery().andEq(User::getId, id).unique();
    // }

}
