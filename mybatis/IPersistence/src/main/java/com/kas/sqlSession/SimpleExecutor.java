package com.kas.sqlSession;

import com.kas.config.BoundSql;
import com.kas.pojo.Configuration;
import com.kas.pojo.MappedStatement;
import com.kas.utils.GenericTokenParser;
import com.kas.utils.ParameterMapping;
import com.kas.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //1.注册驱动 获取连接
        Connection connection = configuration.getDataSource().getConnection();

        //2,3,4获取执行的preparedStatement
        PreparedStatement preparedStatement = getPreparedStatement(connection,mappedStatement,params);

        //5.执行SQL
        ResultSet resultSet = preparedStatement.executeQuery();

        //6.封装返回结果集
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        ArrayList<Object> objects = new ArrayList<>();

        while (resultSet.next()){
            Object o = resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 字段名称
                String columnName = metaData.getColumnName(i);
                // 字段值
                Object value = resultSet.getObject(columnName);
                // 使用反射或者内省,根据数据库表和实体的对应关系,完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }
        return (List<E>) objects;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //1.获取连接
        Connection connection = configuration.getDataSource().getConnection();
        //2.获取执行语句
        PreparedStatement preparedStatement = getPreparedStatement(connection, mappedStatement, params);
        //3.执行语句
        int result = preparedStatement.executeUpdate();
        return result;
    }


    private BoundSql getBoundSql(String sql){
        //标记处理类:配合标记解析器来完成对占位符的处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();

        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析完成的SQL
        String parseSql = genericTokenParser.parse(sql);
        //#{}解析过来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
        return boundSql;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (null != parameterType){
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
        return null;
    }

    private PreparedStatement getPreparedStatement(Connection connection,MappedStatement mappedStatement,Object... params) throws Exception {
        //2.获取sql语句
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        //3.获取预处理对象:preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //4.设置参数
        // 获取参数全路径
        String parameterType = mappedStatement.getParamterType();
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        if ("Integer".equals(parameterType)){
            for (int i = 0; i < parameterMappingList.size(); i++) {
                preparedStatement.setObject(i+1,params[0]);
            }
        }else{
            Class<?> parameterTypeClass = getClassType(parameterType);
            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();

                // 使用反射
                Field declaredFields = parameterTypeClass.getDeclaredField(content);
                declaredFields.setAccessible(true);
                Object o = declaredFields.get(params[0]);
                preparedStatement.setObject(i+1,o);
            }
        }
        return preparedStatement;
    }
}
