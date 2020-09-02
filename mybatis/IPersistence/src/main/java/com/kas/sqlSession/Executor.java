package com.kas.sqlSession;

import com.kas.pojo.Configuration;
import com.kas.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    /**
     * 查询接口
     * @return
     */
    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement,
                      Object... params) throws Exception;

    /**
     * 更新接口
     * @return
     */
    int update(Configuration configuration,MappedStatement mappedStatement,Object... params) throws Exception;
}
