package com.mybatis.test.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ParameterHandler类
 *
 * @author wangjixue
 * @date 8/15/21 11:02 PM
 */
public class ParameterHandler {
    private PreparedStatement prepareStatement;

    public ParameterHandler(PreparedStatement prepareStatement) {
        prepareStatement = prepareStatement;
    }

    /**
     * 从方法中获取参数，遍历设置SQL中的？占位符
     *
     * @param parameters
     */
    public void setParameters(Object[] parameters) throws SQLException {
        // PreparedStatement的序号是从1开始的
        for (int i = 0; i < parameters.length; i++) {
            int index = i + 1;
            if (parameters[i] instanceof Integer) {
                prepareStatement.setInt(index, (Integer) parameters[i]);
            } else if (parameters[i] instanceof Long) {
                prepareStatement.setLong(index, (Long) parameters[i]);
            } else if (parameters[i] instanceof String) {
                prepareStatement.setString(index, String.valueOf(parameters[i]));
            } else if (parameters[i] instanceof Boolean) {
                prepareStatement.setBoolean(index, (Boolean) parameters[i]);
            } else {
                prepareStatement.setString(index, String.valueOf(parameters[i]));
            }
        }
    }
}
