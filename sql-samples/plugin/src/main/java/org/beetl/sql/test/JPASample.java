package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.test.jpa.CrudRepository;

import java.util.Optional;

/**
 * 完全重新定义Mapper实现，参考CrudRepository，JPASupport
 */
public class JPASample {
	SQLManager sqlManager;
	public JPASample(SQLManager sqlManager) {
		this.sqlManager = sqlManager;

	}

	public static void main(String[] args) throws Exception {
		SQLManager sqlManager = SampleHelper.getSqlManager();
		JPASample jpaSample = new JPASample(sqlManager);
		jpaSample.test();
	}

	public void test(){
		MyRepository myRepository = sqlManager.getMapper(MyRepository.class);
		int id = 30;
		UserEntity newUser = new UserEntity();
		newUser.setId(id);
		newUser.setName("ab");
		myRepository.save(newUser);
		UserEntity user = myRepository.getById(id);
		System.out.println(user);

		Optional<UserEntity> optional = myRepository.findById(id);

		System.out.println(optional.get());
	}


	public interface  MyRepository extends CrudRepository<UserEntity,Integer>{
		/**
		 * spring data
		 * @param id
		 * @return
		 */
		UserEntity getById(Integer id);
	}

	@Data
	@Table(name="sys_user")
	public static class UserEntity {
		Integer id;
		String name;
	}


}
