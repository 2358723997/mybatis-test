package com.mybatis.test.executor;

/**
 * Executor接口
 *
 * @author wangjixue
 * @date 8/15/21 9:00 PM
 */
public interface Executor {
    <T> T query(String sql, Object[] parameter, Class pojo);
}
