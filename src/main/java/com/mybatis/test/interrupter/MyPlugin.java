package com.mybatis.test.interrupter;

import java.util.Arrays;

import com.mybatis.test.annotation.Intercepts;
import com.mybatis.test.plugin.Interceptor;
import com.mybatis.test.plugin.Invocation;
import com.mybatis.test.plugin.Plugin;

/**
 * MyPlugin类
 * 自定义插件
 *
 * @author wangjixue
 * @date 8/15/21 7:34 PM
 */
@Intercepts("query")
public class MyPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String statement = (String) invocation.getArgs()[0];
        Object[] parameter = (Object[]) invocation.getArgs()[1];
        Class clazz = (Class) invocation.getArgs()[2];
        System.out.println("进入自定义插件：MyPlugin");
        System.out.println("SQL：["+statement+"]");
        System.out.println("Parameters："+ Arrays.toString(parameter));
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }
}
