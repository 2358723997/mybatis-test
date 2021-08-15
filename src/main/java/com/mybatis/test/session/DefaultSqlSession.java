package com.mybatis.test.session;

import com.mybatis.test.executor.Executor;
import com.mybatis.test.mapper.BlogMapper;

/**
 * DefaultSqlSession类
 *
 * MeBatis的API，提供给应用层使用
 *
 * @author wangjixue
 * @date 8/15/21 7:23 PM
 */
public class DefaultSqlSession {
    private Configuration configuration;
    private Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        // 根据全局配置决定是否使用缓存装饰
        this.executor = configuration.newExecutor();
    }

    public <T> T getMapper(Class<T> clazz) {
        return configuration.getMapper(clazz, this);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Object selectOne(String statementId, Object[] parameter, Class pojo) {
        String sql = configuration.getMappedStatement(statementId);
        // 打印代理对象时会自动调用toString()方法，触发invoke()
        return executor.query(sql, parameter, pojo);
    }
}
