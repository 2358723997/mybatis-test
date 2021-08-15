package com.mybatis.test.executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mybatis.test.parameter.ParameterHandler;
import com.mybatis.test.session.Configuration;

/**
 * StatementHandler类
 * 封装JDBC Statement，用于操作数据库
 *
 * @author wangjixue
 * @date 8/15/21 10:58 PM
 */
public class StatementHandler {
    private ResultSetHandler resultSetHandler = new ResultSetHandler();

    public <T> T query(String sql, Object[] parameter, Class pojo) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        Object result = null;
        try {
            connection = getConnection();
            prepareStatement = connection.prepareStatement(sql);
            ParameterHandler parameterHandler = new ParameterHandler(prepareStatement);
            parameterHandler.setParameters(parameter);
            prepareStatement.execute();
            try {
                result = resultSetHandler.handle(prepareStatement.getResultSet(), pojo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return (T) result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                connection = null;
            }
        }
        // 只在try里面return会报错
        return null;
    }

    /**
     * 获取连接
     *
     * @return
     */
    private Connection getConnection() {
        String driver = Configuration.properties.getString("jdbc.driver");
        String url = Configuration.properties.getString("jdbc.url");
        String user = Configuration.properties.getString("jdbc.user");

        String password = Configuration.properties.getString("jdbc.password");
        Connection conn = null;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
