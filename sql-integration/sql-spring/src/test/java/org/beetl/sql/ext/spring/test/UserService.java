package org.beetl.sql.ext.spring.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserMapper userMapper;
    public void addUser(UserInfo userInfo){
        userMapper.insert(userInfo);
    }

    public void delete(Long id){

       System.out.print(userMapper.deleteById(id));
       if(1==1){
           throw new RuntimeException("test transation");
       }
    }

    public UserInfo selectOne(){
    	return userMapper.select();
	}

    /**
     * 只有readOnly=true，才会在主从下走从数据库
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public UserInfo getById(Long id){
        return userMapper.single(id);
    }
    public long count(){
        return userMapper.allCount();
    }
}
