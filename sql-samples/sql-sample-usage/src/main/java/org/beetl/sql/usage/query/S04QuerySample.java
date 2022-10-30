package org.beetl.sql.usage.query;

import lombok.Data;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.page.DefaultPageResult;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 演示如何使用query构造查询条件
 * 方法调用顺序应保持为
 * --> and ,or(大于、小于、等于、LIKE、BETWEEN、IN、NOT IN等)
 * --> groupBy --> having
 * --> orderBy(desc,asc 等价，只能用一种 )
 * --> limit(page等价，只能用一种)
 * --> select(update, delete, insert，page等价)
 * @author xiandafu
 */
public class S04QuerySample {

    SQLManager sqlManager;

    public S04QuerySample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public static void main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S04QuerySample sample = new S04QuerySample(sqlManager);
//        sample.basic();
//        sample.order();
//        sample.like();
//        sample.in();
//        sample.selectSingle();
//        sample.page();
//        sample.orAnd();
        sample.andBetween();
//        sample.filterEmpty();
//        sample.groupBy();


    }

    public void basic(){
        Query<UserEntity> query = sqlManager.query(UserEntity.class);
        List<UserEntity> list = query.andEq("name","ok")
                .andEq("department_id",1).select();
        //使用字符串表示要查询的列的问题是如果数据库重构，则会报错,因此推荐使用LambdaQuery
        LambdaQuery<UserEntity> lambdaQuery = sqlManager.lambdaQuery(UserEntity.class);

        list = lambdaQuery.andEq(UserEntity::getName,"ok")
                .andEq(UserEntity::getDepartmentId,1).select();


        //重用query类，忽略blob，clob这种列
        list = query.selectSimple();

    }

    public void order(){
        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        List<UserEntity> list = query.andEq(UserEntity::getName,"ok")
                .orEq(UserEntity::getId,1)
                .andGreat(UserEntity::getDepartmentId,3)
                .asc(UserEntity::getName)
                //只需要id和name
                .select(UserEntity::getId,UserEntity::getName);
        System.out.println(list.size());




    }


    public void like(){
        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        List<UserEntity> list = query.andLike(UserEntity::getName,"%ok%")
                .select();
        System.out.println(list.size());

    }



    public void in(){
        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        List<UserEntity> list = query.andIn(UserEntity::getId, Arrays.asList(1,2,3))
                .select();
        System.out.println(list.size());

    }

    /**
     * 只选择一条
     */
    public void selectSingle(){
        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        UserEntity user = query.andEq(UserEntity::getId, 1).single();
        UserEntity user2 = query.andEq(UserEntity::getId, 1).unique();
    }

    /**
     * Query通常更适合业务处理，而不适合界面查询，这里演示了一个翻页查询
     */
    public void page(){
        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        PageResult result = query.andEq(UserEntity::getId, 1).page(1,10);
        SampleHelper.printPageResult((DefaultPageResult)result);
    }

    /**
     * where xx or ( xxx and yy);
     */
    public void orAnd(){
        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        query.andEq(UserEntity::getId, 2)
                .or(query.condition().andEq(UserEntity::getId, 1).andEq(UserEntity::getDepartmentId,1));
        List<UserEntity> list = query.select();
    }

	/**
	 * where xx or ( xxx and yy);
	 */
	public void andBetween(){
		LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
		LambdaQuery condition = query.condition().andBetween(UserEntity::getId, 1,3);
		query.andEq(UserEntity::getId, 2)
				.and(condition);
		List<UserEntity> list = query.select();
	}

    /**
     * 如果变量为空，则忽略
     */
    public void filterEmpty(){
        //输入变量为null
        String name=  null;
        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        query.andEq(UserEntity::getName,Query.filterNull(name));
        long count = query.count();
        //考虑集合或者字符串为空
        name="";
        query = sqlManager.lambdaQuery(UserEntity.class);
        query.andEq(UserEntity::getName,Query.filterEmpty(name));
        count = query.count();

    }


    /**
     * Query能提供简单的group实现
     * 建议group by还是通过mapper或者sqlId方式来实现，因为直接通过sql更容易阅读
     */
    public void groupBy(){

        LambdaQuery<UserEntity> query = sqlManager.lambdaQuery(UserEntity.class);
        List<Map> list = query.andIsNotNull(UserEntity::getDepartmentId)
                .groupBy(UserEntity::getName).select(Map.class,"count(name) as total","name");
        System.out.println(list);

       List<GroupCount>  groupList = query.andIsNotNull(UserEntity::getDepartmentId)
			   .groupBy(UserEntity::getName).select(GroupCount.class,"count(name) as total","name");
       System.out.println(groupList);

    }

    @Data
    public static class GroupCount{
        Integer total;
        String name;
    }


}
