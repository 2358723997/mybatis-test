package com.mybatis.test.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * InterceptorChain类
 *
 * @author wangjixue
 * @date 8/15/21 8:34 PM
 */
public class InterceptorChain {
    private final List<Interceptor> interceptors = new ArrayList<>();


    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public boolean hasPlugin() {
        return !interceptors.isEmpty();
    }

    /**
     * 对被拦截对象进行层层代理
     *
     * @param target
     * @return
     */
    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }
}
