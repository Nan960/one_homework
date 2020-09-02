package com.kas.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件容器对象
 */
public class Configuration {

    private DataSource dataSource;

    /**
     * key=>statementID value=>封装好的mappedStatement对象
     */
    Map<String,MappedStatement> mappedStatementMap = new HashMap<>();


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }
}
