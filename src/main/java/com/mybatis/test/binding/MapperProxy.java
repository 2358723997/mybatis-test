package com.mybatis.test.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.mybatis.test.session.DefaultSqlSession;

/**
 * MapperProxy类
 * MapperProxy代理类，用于代理Mapper接口
 *
 * @author wangjixue
 * @date 8/15/21 9:16 PM
 */
public class MapperProxy implements InvocationHandler {

    private DefaultSqlSession sqlSession;
    private Class pojo;

    public MapperProxy(DefaultSqlSession sqlSession, Class pojo) {
        this.sqlSession = sqlSession;
        this.pojo = pojo;
    }

    /**
     * 所有Mapper接口的方法调用都会走到这里
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String mapperInterface = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String statementId = mapperInterface + methodName;
        // 如果根据接口类型+方法名能找到映射的SQL，则执行SQL
        if (sqlSession.getConfiguration().hasStatement(statementId)) {
            return sqlSession.selectOne(statementId, args, pojo);
        }
        // 否则直接执行被代理对象的原方法
        return method.invoke(proxy, args);
    }
}
