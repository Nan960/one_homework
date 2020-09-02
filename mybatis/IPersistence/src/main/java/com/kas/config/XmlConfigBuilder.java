package com.kas.config;

import com.kas.io.Resources;
import com.kas.pojo.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XmlConfigBuilder {

    private Configuration configuration;

    public XmlConfigBuilder(){
        this.configuration = new Configuration();
    }

    /**
     * 解析xml文件 将配置文件进行解析封装到configuration
     */
    public Configuration parseConfig(InputStream in) throws DocumentException, PropertyVetoException {
        Document document = new SAXReader().read(in);
        //<configuration>
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for (Element element : list) {
            String name  = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name,value);
        }


        //使用c3p0连接池
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));


        //将数据库连接池放入到配置中
        configuration.setDataSource(comboPooledDataSource);

        //mapper.xml解析: 拿到路径--字节输入流-dom4j解析
        List<Element> mapperList = rootElement.selectNodes("//mapper");
        for (Element element : mapperList) {
            //解析配置的mapper.xml文件
            String mapperPath = element.attributeValue("resource");
            InputStream resourceAsStream = Resources.getResourceAsStream(mapperPath);
            XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder(configuration);
            //将xml文件进行解析
            xmlMapperBuilder.parse(resourceAsStream);
        }

        return configuration;
    }
}
