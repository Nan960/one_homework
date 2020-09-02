package com.kas.test;

import com.kas.dao.IUserDao;
import com.kas.io.Resources;
import com.kas.pojo.User;
import com.kas.sqlSession.SqlSession;
import com.kas.sqlSession.SqlSessionFactory;
import com.kas.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

/**
 * 测试类
 */
public class IPersistenceTest {
    private IUserDao userDao = null;

    @Before
    public void init()throws Exception{
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        userDao = sqlSession.getMapper(IUserDao.class);
    }

    /**
     * 查询
     * @throws Exception
     */
    @Test
    public void testSelect() throws Exception {
        List<User> userList = userDao.findAll();
        for (User user : userList) {
            System.out.println(user);
        }
    }

    @Test
    public void testUpdate() throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("tom");
        int i = userDao.updateUser(user);
    }

    @Test
    public void testInsert() throws Exception{
        User user = new User();
        user.setId(5);
        user.setName("nico");
        int i = userDao.insertUser(user);
    }

    @Test
    public void testDelete() throws Exception{
        int i = userDao.deleteUser(5);
    }
}
