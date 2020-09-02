package com.kas.config;

import com.kas.pojo.Configuration;
import com.kas.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlMapperBuilder {

    private Configuration configuration;

    public XmlMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream in) throws DocumentException {
        Document document = new SAXReader().read(in);
        Element rootElement = document.getRootElement();

        //获取namespace名称
        String nameSpace = rootElement.attributeValue("namespace");

        //所有操作的集合
        List<Element> allList = new ArrayList<>();
        //获取select语句
        List<Element> selectList = rootElement.selectNodes("//select");
        List<Element> updateList = rootElement.selectNodes("//update");
        List<Element> deleteList = rootElement.selectNodes("//delete");
        List<Element> insertList = rootElement.selectNodes("//insert");
        //添加到集合中
        allList.addAll(selectList);
        allList.addAll(updateList);
        allList.addAll(deleteList);
        allList.addAll(insertList);

        for (Element element : allList) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            // sql语句
            String sqlText = element.getTextTrim();
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParamterType(paramterType);
            mappedStatement.setSql(sqlText);

            String key = nameSpace + "." + id;
            configuration.getMappedStatementMap().put(key,mappedStatement);
        }
    }
}
