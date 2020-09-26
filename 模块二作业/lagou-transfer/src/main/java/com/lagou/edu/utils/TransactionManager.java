package com.lagou.edu.utils;

import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyService;

import java.sql.SQLException;

/**
 * @author 应癫
 *
 * 事务管理器类：负责手动事务的开启、提交、回滚
 */
@MyService("transactionManager")
public class TransactionManager {

    @MyAutowired
    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    // 开启手动事务控制
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
        System.out.println("开启事务");
    }


    // 提交事务
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
        System.out.println("事务提交");
    }


    // 回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
        System.out.println("事务操作回滚");
    }
}
