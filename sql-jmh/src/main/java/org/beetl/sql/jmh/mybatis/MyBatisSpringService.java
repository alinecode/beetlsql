package org.beetl.sql.jmh.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.beetl.sql.jmh.base.BaseService;
import org.beetl.sql.jmh.mybatis.vo.MyBatisSysCustomerView;
import org.beetl.sql.jmh.mybatis.vo.MyBatisSysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MyBatisSpringService implements BaseService {
    AtomicInteger idGen = new AtomicInteger(1000);

    @Autowired
    MyBatisUserRepository myBatisUserRepository;
    @Override
    public void addEntity() {
        MyBatisSysUser user = new MyBatisSysUser();
        user.setId(idGen.getAndIncrement());
        user.setCode("abc");
		user.setCode1("abc");
		user.setCode2("abc");
		user.setCode3("abc");
		user.setCode4("abc");
		user.setCode5("abc");
		user.setCode6("abc");
		user.setCode7("abc");
		user.setCode8("abc");
		user.setCode9("abc");
		user.setCode10("abc");
		user.setCode11("abc");
		user.setCode12("abc");
		user.setCode13("abc");
		user.setCode14("abc");
		user.setCode15("abc");
		user.setCode16("abc");
		user.setCode17("abc");
		user.setCode18("abc");
		user.setCode19("abc");
		user.setCode20("abc");
        myBatisUserRepository.insert(user);

    }

    @Override
    @Transactional(readOnly=true)
    public Object getEntity() {
        MyBatisSysUser user = myBatisUserRepository.selectById(1);
        return user;
    }

    @Override
    public void lambdaQuery() {
        QueryWrapper<MyBatisSysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MyBatisSysUser::getId, 1);
        List<MyBatisSysUser> list = myBatisUserRepository.selectList(queryWrapper);
    }

    @Override
    public void executeJdbcSql() {
       throw new UnsupportedOperationException();
    }

    @Override
    public void executeTemplateSql() {
        MyBatisSysUser user = myBatisUserRepository.selectEntityById(1);
    }

    @Override
    public void sqlFile() {

        MyBatisSysUser user = myBatisUserRepository.selectUser(1);
    }

    @Override
    public void one2Many() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pageQuery() {
        QueryWrapper<MyBatisSysUser> entityWrapper = new QueryWrapper<>();
        MyBatisSysUser sysUser = new  MyBatisSysUser();
        sysUser.setCode("用户一");
        entityWrapper.setEntity(sysUser);
        Page<MyBatisSysUser> page = new Page<>(1,5);
        IPage<MyBatisSysUser> iPage = myBatisUserRepository.selectPage(page, entityWrapper);
        iPage.getRecords();
    }

    @Override
    public void complexMapping() {
        MyBatisSysCustomerView view = myBatisUserRepository.selectView(1);
        view.getOrder().size();
    }

	@Override
	public void getAll() {
		List<MyBatisSysUser> myBatisSysUsers = myBatisUserRepository.selectEntities();
	}
}
