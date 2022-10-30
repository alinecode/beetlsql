package org.beetl.sql.usage.sqlmanger;

import lombok.Data;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany;
import org.beetl.sql.fetch.annotation.FetchOne;
import org.beetl.sql.sample.SampleHelper;

import java.util.List;
import java.util.Objects;

/**
 * 演示使用@Fetch 自动抓取，可以参考S5Fetch。 这里演示一个嵌套多层抓取
 * 仅仅需要设置Level
 */
public class S10FetchSample {
    SQLManager sqlManager;

    public S10FetchSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public static void main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S10FetchSample sample = new S10FetchSample(sqlManager);
        sample.fetchOne();
    }

    /**
     * 自动抓取用户的部门信息，并且部门信息也自动抓取部门的所有用户信息
     */
    public void fetchOne(){
        UserData user = sqlManager.unique(UserData.class,1);
        DepartmentData departmentData = user.getDept();
        System.out.println(departmentData.getName());
        List<UserData> dataUsers = departmentData.getUsers();
        System.out.println(dataUsers.size());

    }





    @Data
    @Table(name="sys_user")
    //设置fetch leve 为 2，导致会向下抓取2层
    @Fetch(level = 2)
    public static class UserData {
        @Auto
        private Integer id;
        private String name;
        private Integer departmentId;
        @FetchOne("departmentId")
        private DepartmentData dept;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			UserData userData = (UserData) o;
			return id.equals(userData.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
	}


    @Data
    @Table(name="department")
    @Fetch
    public static class DepartmentData {
        @Auto
        private Integer id;
        private String name;
        @FetchMany("departmentId")
        private List<UserData> users;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			DepartmentData that = (DepartmentData) o;
			return id.equals(that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
	}

}
