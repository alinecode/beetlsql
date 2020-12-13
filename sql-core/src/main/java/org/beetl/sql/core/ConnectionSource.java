package org.beetl.sql.core;

import org.beetl.sql.clazz.kit.BeetlSQLException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Beetlsql 使用ConnectionSource管理 数据库连接。这有点类似DataSource
 * 主要区别在于getConnection方法，ConnectionSource更加灵活，可以依据ExecuteContext来动态判断应该返回一个什么样的Connection，从而
 * 实现分库，主从库。
 * 需要注意的是，与{@link ConditionalSQLManager} 不同，{@link ConditionalSQLManager}  可以管理多个SQLManager类，而SQLManager可以管理多个ConnectionSource更加灵活
 * 以下是一些常见场景
 * <ui>
 *     <li>有主从库，因此只需要一个SQLManager，然后使用DefaultConnectionSource即可，DefaultConnectionSource可以代理主从库</li>
 *     <li>有俩个库，一个库存业务数据，一个库存查询分析数据，那需要俩个SQLManager，对应各自库，为了方便操作，可以使用{@link ConditionalSQLManager} 动态决定使用哪个库</li>
 * 	   <li>我的业务数据需要分库，比如订单分到不同库，我可以使用一个SQLManager，然后使用ConditionalConnectionSource来根据业务信息返回不同的数据库连接</li>
 * 	   <li>更复杂情况是以上综合，业务库是分库，但业务的查询报表是另外一个NOSQL或者查询引擎，因此需要综合ConditionalSQLManager和ConditionalConnectionSource，
 * 	   从而实现对业务层来说，就是一个SQLManger（或者衍生出来的Mapper）
 * 	   </li>
 * </ui>
 *
 * @author xiandafu
 * @see ConnectionSourceHelper
 * @see DefaultConnectionSource
 * @see ConditionalConnectionSource
 */
public interface ConnectionSource {
	/**
	 * 得到一个主库连接,主要用于
	 *
	 * @return
	 */
	Connection getMasterConn();


	/**
	 * 得到一个获取数据库metadata，如果是分库分表，或者主从，需要给出一个能获得数据库信息的链接
	 * 通常这是 主库。如果你只有一个数据，那就同{@code #getMaster}
	 *
	 * @return
	 */
	Connection getMetaData();


	/**
	 * 根据条件得到链接
	 *
	 * @param ctx      执行上下文
	 * @param isUpdate 是否更新数据
	 * @return
	 */
	Connection getConn(ExecuteContext ctx, boolean isUpdate);

	/**
	 * 是否是事务环境，这个是与web框架整合的框架，通常web框架都有api可以得到是否还在事务环境。
	 * 如果不是，则beetlsql可以自主管理事务，否则，交给web框架管理。
	 *
	 * 通常web框架集成，这个返还true，则beetlsql不在，做提交事务的事情，交给web框架统一管理
	 *
	 * @return
	 */
	boolean isTransaction();


	default void applyStatementSetting(ExecuteContext ctx,Connection conn,Statement statement) throws SQLException {

	}


	default void applyConnectionSetting(ExecuteContext ctx,Connection conn){

	}

	default void closeConnection(Connection conn,ExecuteContext ctx,boolean isUpdate){
		if (!isTransaction()) {
			try {

				if (conn != null) {
					// colse 不一定能保证能自动commit
					if (isUpdate && !conn.getAutoCommit()) {

						conn.commit();
					}

					conn.close();
				}
			} catch (SQLException e) {
				throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
			}

		}
	}



}
