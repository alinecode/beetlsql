/* 存储过程 mysql */


CREATE PROCEDURE test.mytest(OUT s_count INT,OUT s_count2 varchar(10))

BEGIN
		-- DECLARE声明 用来声明变量的
		DECLARE de_name VARCHAR(10) DEFAULT '';

		SET de_name = "jim";

		-- 测试输出语句（不同的数据库，测试语句都不太一样。
		SELECT de_name;
		SET s_count =0;
		SET s_count2='abc';
	END




