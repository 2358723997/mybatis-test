package com.mybatis.test.executor;

import java.sql.Statement;

/**
 * SimpleExecutor类
 *
 * @author wangjixue
 * @date 8/15/21 9:03 PM
 */
public class SimpleExecutor implements Executor {

    @Override
    public <T> T query(String sql, Object[] parameter, Class pojo) {
        StatementHandler handler = new StatementHandler();
        return handler.query(sql,parameter,pojo);
    }
}
