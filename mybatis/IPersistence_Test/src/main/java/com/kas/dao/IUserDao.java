package com.kas.dao;

import com.kas.pojo.User;

import java.util.List;

public interface IUserDao {

    List<User> findAll() throws Exception;

    /**
     * 根据条件查询user
     * @param user
     * @return
     */
    User findByCondition(User user) throws Exception;

    /**
     * 更新用户
     */
    Integer updateUser(User user) throws Exception;

    /**
     * 新增用户
     * @return
     */
    Integer insertUser(User user) throws Exception;

    /**
     * 删除用户
     * @return
     */
    Integer deleteUser(Integer id) throws Exception;
}
