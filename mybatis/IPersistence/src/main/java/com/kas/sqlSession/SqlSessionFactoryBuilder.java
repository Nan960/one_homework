package com.kas.sqlSession;

import com.kas.config.XmlConfigBuilder;
import com.kas.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream in) throws PropertyVetoException, DocumentException {
        //第一: 使用dom4j解析xml文件 将解析出来的内容封装到Configuration中
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(in);


        // 第二: 创建sqlSessionFactory对象==>工厂类:生产sqlSession:会话对象
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);

        return defaultSqlSessionFactory;
    }
}
