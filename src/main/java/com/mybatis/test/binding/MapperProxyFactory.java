package com.mybatis.test.binding;

import java.lang.reflect.Proxy;

import com.mybatis.test.session.DefaultSqlSession;

/**
 * MapperProxyFactory类
 * 用于产生MapperProxy代理类
 *
 * @author wangjixue
 * @date 8/15/21 9:10 PM
 */
public class MapperProxyFactory<T> {

    private Class<T> mapperInterface;
    private Class pojo;

    public MapperProxyFactory(Class mapperInterface, Class pojo) {
        mapperInterface = mapperInterface;
        pojo = pojo;
    }

    public T newInstance(DefaultSqlSession sqlSession) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, new MapperProxy(sqlSession, pojo));
    }
}
