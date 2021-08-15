package com.mybatis.test;

import com.mybatis.test.mapper.Blog;
import com.mybatis.test.mapper.BlogMapper;
import com.mybatis.test.session.DefaultSqlSession;
import com.mybatis.test.session.SqlSessionFactory;

/**
 * @Author: qingshan
 */
public class TestMybatis {

    public static void main(String[] args) {
        SqlSessionFactory factory = new SqlSessionFactory();
        DefaultSqlSession sqlSession = factory.build().openSqlSession();
        // 获取MapperProxy代理
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Blog blog = mapper.selectBlogById(1);

        System.out.println("第一次查询: " + blog);
        System.out.println();
        blog = mapper.selectBlogById(1);
        System.out.println("第二次查询: " + blog);
    }
}
