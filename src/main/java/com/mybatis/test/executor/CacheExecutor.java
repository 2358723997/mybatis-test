package com.mybatis.test.executor;

import java.util.HashMap;
import java.util.Map;

import com.mybatis.test.cache.CacheKey;

/**
 * CacheExecutor类
 * 带缓存的执行器，用于装饰基本执行器
 *
 * @author wangjixue
 * @date 8/15/21 9:03 PM
 */
public class CacheExecutor implements Executor {
    private Executor delegate;
    private static final Map<Integer, Object> cache = new HashMap<>();

    public CacheExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T query(String sql, Object[] parameter, Class pojo) {
        // 计算CacheKey
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(sql);
        cacheKey.update(joinStr(parameter));
        // 是否拿到缓存
        if (cache.containsKey(cacheKey.getCode())) {
            // 命中缓存
            return (T) cache.get(cacheKey.getCode());
        } else {
            // 没有的话调用被装饰的SimpleExecutor从数据库查询
            Object result = delegate.query(sql, parameter, pojo);
            cache.put(cacheKey.getCode(), result);
            return (T) result;
        }
    }

    /**
     * 为了命中缓存，把Object[]转换成逗号拼接的字符串，因为对象的HashCode都不一样
     *
     * @param parameter
     * @return
     */
    private String joinStr(Object[] parameter) {
        StringBuffer sb = new StringBuffer();
        if (parameter != null && parameter.length > 0) {
            for (Object objStr : parameter) {
                sb.append(objStr.toString() + ",");
            }
        }
        int len = sb.length();
        if (len > 0) {
            sb.deleteCharAt(len - 1);
        }
        return sb.toString();
    }
}
