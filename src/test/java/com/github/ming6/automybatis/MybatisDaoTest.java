package com.github.ming6.automybatis;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.ming6.automybatis.config.MybatisConfig;
import com.github.ming6.automybatis.dao.UserDao;
import com.github.ming6.automybatis.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MybatisConfig.class)
public class MybatisDaoTest {
	
	@Autowired
	private UserDao userDao;

	@Test
	public void testInsert(){
		User user = new User();
		user.setName("userA");
		userDao.insert(user);
	}
	
	@Test
	public void testUpdate(){
		User user = new User();
		user.setId(1L);
		user.setName("userB");
		userDao.update(user);
	}
	
	@Test
	public void testSelectList(){
		User user = new User();
		user.setName("userA");
		List<User> users = userDao.selectList(user, 0, 1);
		for(User u : users){
			System.out.println(u.getName());
		}
	}
}