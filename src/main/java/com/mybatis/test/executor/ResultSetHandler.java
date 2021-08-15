package com.mybatis.test.executor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResultSetHandler类
 * 结果集处理器
 *
 * @author wangjixue
 * @date 8/15/21 11:05 PM
 */
public class ResultSetHandler {
    /**
     * @param resultSet 结果集
     * @param type      需要转换的目标类型
     * @return
     */
    public <T> T handle(ResultSet resultSet, Class type) {
        // 直接调用Class的newInstance方法产生一个实例
        Object pojo = null;
        try {
            pojo = type.newInstance();
            // 遍历结果集
            if (resultSet.next()) {
                for (Field field : pojo.getClass().getDeclaredFields()) {
                    setValue(pojo, field, resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) pojo;
    }

    /**
     * 通过反射给属性赋值
     *
     * @param pojo
     * @param field
     * @param resultSet
     */
    private void setValue(Object pojo, Field field, ResultSet resultSet) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {
        // 获取 pojo 的 set 方法
        Method method = pojo.getClass().getMethod("set" + firstWordCapital(field.getName()), field.getType());
        // 调用 pojo 的set 方法，使用结果集给属性赋值
        // 赋值先从resultSet取出值
        method.invoke(pojo, getResult(resultSet, field));
    }

    /**
     * 根据反射判断类型，从ResultSet中取对应类型参数
     *
     * @param resultSet
     * @param field
     * @return
     */
    private Object getResult(ResultSet resultSet, Field field) throws SQLException {
        //TODO TypeHandler
        Class<?> type = field.getType();
        String dataName = humpToUnderline(field.getName()); // 驼峰转下划线;
        // TODO 类型判断不够全
        if (Integer.class == type) {
            return resultSet.getInt(dataName);
        } else if (String.class == type) {
            return resultSet.getString(dataName);
        } else if (Long.class == type) {
            return resultSet.getLong(dataName);
        } else if (Boolean.class == type) {
            return resultSet.getBoolean(dataName);
        } else if (Double.class == type) {
            return resultSet.getDouble(dataName);
        } else {
            return resultSet.getString(dataName);
        }
    }

    /**
     * 数据库下划线转Java驼峰命名
     *
     * @param para
     * @return
     */
    private String humpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        if (!para.contains("_")) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, "_");
                    temp += 1;
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 单词首字母大写
     *
     * @param name
     * @return
     */
    private String firstWordCapital(String name) {
        String first = name.substring(0, 1);
        String tail = name.substring(1);
        return first.toUpperCase() + tail;
    }
}
