package com.mybatis.test.mapper;

import com.mybatis.test.annotation.Entity;
import com.mybatis.test.annotation.Select;

/**
 * @Author: qingshan
 */
@Entity(Blog.class)
public interface BlogMapper {
    /**
     * 根据主键查询文章
     * @param bid
     * @return
     */
    @Select("select * from blog where bid = ?")
    public Blog selectBlogById(Integer bid);

}
