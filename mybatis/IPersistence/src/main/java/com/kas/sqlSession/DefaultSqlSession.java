package com.kas.sqlSession;

import com.kas.pojo.Configuration;
import com.kas.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession{

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {

        //使用simpleExecutor的query方法
        SimpleExecutor executor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<E> query = (List<E>) executor.query(configuration, mappedStatement, params);
        return query;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId,params);
        if (objects.size() != 0){
            if (objects.size() == 1){
                return (T) objects.get(0);
            }else {
                throw new RuntimeException("查询错误:结果过多");
            }
        }
        throw new RuntimeException("查询错误:结果为空");
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        //使用simpleExecutor的update方法
        SimpleExecutor executor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        int update = executor.update(configuration, mappedStatement, params);
        return update;
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        //使用simpleExecutor的update方法
        SimpleExecutor executor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        int update = executor.update(configuration, mappedStatement, params);
        return update;
    }

    @Override
    public int add(String statementId, Object... params) throws Exception {
        //使用simpleExecutor的update方法
        SimpleExecutor executor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        int update = executor.update(configuration, mappedStatement, params);
        return update;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象,并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //底层执行的是JDBC代码
                //参数准备
                //1.statementId=>Sql语句的唯一标识
                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();

                String statementId= className + "."+ methodName;

                //2.参数params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                //判断是否进行了 泛型类型参数化
                if (genericReturnType instanceof ParameterizedType){
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }else {
                    int update = update(statementId, args);
                    return update;
                }
            }
        });
        return (T) proxyInstance;
    }
}
