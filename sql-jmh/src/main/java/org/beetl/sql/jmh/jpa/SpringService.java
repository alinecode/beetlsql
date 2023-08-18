package org.beetl.sql.jmh.jpa;

import org.beetl.sql.jmh.BaseService;
import org.beetl.sql.jmh.jpa.vo.JpaSysCustomer;
import org.beetl.sql.jmh.jpa.vo.JpaSysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class SpringService implements BaseService {
    AtomicInteger idGen = new AtomicInteger(1000);
    @Autowired
    JpaUserMapper jpaUserMapper;
    @Autowired
    EntityManager em;

    @Override
    public void addEntity() {
        JpaSysUser user = new JpaSysUser();
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
        jpaUserMapper.save(user);

    }

    @Override
    @Transactional(readOnly=true)
    public Object getEntity() {
		em.clear();
        return jpaUserMapper.selectById(1);
    }

    @Override
    public void lambdaQuery() {
      throw new UnsupportedOperationException();

    }

    @Override
    public void executeJdbcSql() {
        JpaSysUser user = jpaUserMapper.selectById(1);
    }

    @Override
    public void executeTemplateSql() {
        JpaSysUser user  = jpaUserMapper.selectTemplateById(1);
    }

    @Override
    public void sqlFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void one2Many() {
        JpaSysCustomer customer = em.find(JpaSysCustomer.class,1);
        Integer count = customer.getOrder().size();
    }

    @Override
    public void pageQuery() {
        PageRequest pageRequest = PageRequest.of(0,5);
        Page<JpaSysUser>  ret = jpaUserMapper.pageQuery("用户一",pageRequest);
        List<JpaSysUser> list = ret.getContent();

    }

    @Override
    public void complexMapping() {
        throw new UnsupportedOperationException();
    }

	@Override
	public void getAll() {
		List<JpaSysUser> all = jpaUserMapper.findAll();
	}
}
