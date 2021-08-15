package com.mybatis.test.cache;

/**
 * CacheKey类
 * 缓存Key
 *
 * @author wangjixue
 * @date 8/15/21 11:33 PM
 */
public class CacheKey {
    private static final int DEFAULT_HASHCODE = 17; // 默认哈希值
    private static final int DEFAULT_MULTIPLIER = 37; // 倍数

    private int hashCode;
    private int count;
    private int multiplier;

    public CacheKey() {
        hashCode = DEFAULT_HASHCODE;
        multiplier = DEFAULT_MULTIPLIER;
        count = 0;
    }

    /**
     * 计算CacheKey中的HashCode
     *
     * @param object
     */
    public void update(Object object) {
        int baseHashCode = object == null ? 1 : object.hashCode();
        count++;
        baseHashCode *= count;
        hashCode = baseHashCode + multiplier * hashCode;
    }

    /**
     * 返回CacheKey的值
     *
     * @return
     */
    public int getCode() {
        return hashCode;
    }
}
