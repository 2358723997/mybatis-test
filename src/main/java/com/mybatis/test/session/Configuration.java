package com.mybatis.test.session;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.mybatis.test.TestMybatis;
import com.mybatis.test.annotation.Entity;
import com.mybatis.test.annotation.Select;
import com.mybatis.test.binding.MapperRegistry;
import com.mybatis.test.executor.CacheExecutor;
import com.mybatis.test.executor.Executor;
import com.mybatis.test.executor.SimpleExecutor;
import com.mybatis.test.plugin.Interceptor;
import com.mybatis.test.plugin.InterceptorChain;

/**
 * Configuration类
 * 全局配置类
 *
 * @author wangjixue
 * @date 8/15/21 7:23 PM
 */
public class Configuration {
    // SQL映射关系配置，使用注解时不用重复配置
    public static final ResourceBundle sqlMappings;
    // 全局配置
    public static final ResourceBundle properties;
    // 维护接口与工厂类关系
    public static final MapperRegistry MAPPER_REGISTRY = new MapperRegistry();
    // 维护接口方法与SQL关系
    public static final Map<String, String> mappedStatements = new HashMap<>();
    // 所有Mapper接口
    private List<Class<?>> mapperList = new ArrayList<>();
    // 插件
    private InterceptorChain interceptorChain = new InterceptorChain();
    // 类所有文件
    private List<String> classPaths = new ArrayList<>();

    static {
        sqlMappings = ResourceBundle.getBundle("sql");
        properties = ResourceBundle.getBundle("mybatis");
    }

    /**
     * 初始化时解析全局配置文件
     */
    public Configuration() {
        // Note：在properties和注解中重复配置SQL会覆盖
        // 1.解析sql.properties
        for (String key : sqlMappings.keySet()) {
            Class mapper = null;
            String statement = null;
            String pojoStr = null;
            Class pojo = null;
            // properties中的value用--隔开，第一个是SQL语句
            statement = sqlMappings.getString(key).split("--")[0];
            // properties中的value用--隔开，第二个是需要转换的POJO类型
            pojoStr = sqlMappings.getString(key).split("--")[1];
            // properties中的key是接口类型+方法
            // 从接口类型+方法中截取接口类型
            try {
                mapper = Class.forName(key.substring(0, key.lastIndexOf(".")));
                pojo = Class.forName(pojoStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MAPPER_REGISTRY.addMapper(mapper, pojo); // 接口与返回的实体类关系
            mappedStatements.put(key, statement); // 接口方法与SQL关系
        }
        // 2.解析接口上的注解（会覆盖XML中的接口与实体类的关系）
        String mapperPath = properties.getString("mapper.path");
        scanPackage(mapperPath);
        for (Class<?> mapper : mapperList) {
            parsingClass(mapper);
        }
        // 3.解析插件，可配置多个插件
        String pluginPath = properties.getString("plugin.path");
        String[] pluginPaths = pluginPath.split(",");
        if (pluginPaths != null) {
            // 将插件添加到interceptorChain中
            for (String plugin : pluginPaths) {
                Interceptor interceptor = null;
                try {
                    interceptor = (Interceptor) Class.forName(plugin).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                interceptorChain.addInterceptor(interceptor);
            }
        }
    }

    /**
     * 解析Mapper接口上配置的注解（SQL语句）
     *
     * @param mapper
     */
    private void parsingClass(Class<?> mapper) {
        // 1.解析类上的注解
        // 如果有Entity注解，说明是查询数据库的接口
        if (mapper.isAnnotationPresent(Entity.class)) {
            for (Annotation annotation : mapper.getAnnotations()) {
                if (annotation.annotationType().equals(Entity.class)) {
                    // 注册接口与实体类的映射关系
                    MAPPER_REGISTRY.addMapper(mapper, ((Entity) annotation).value());
                }
            }
        }
        // 2.解析方法上的注解
        Method[] methods = mapper.getMethods();
        for (Method method : methods) {
            // 解析@Select注解的SQL语句
            if (method.isAnnotationPresent(Select.class)) {
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    if (annotation.annotationType().equals(Select.class)) {
                        // 注册接口类型+方法名和SQL语句的映射关系
                        String statement = method.getDeclaringClass().getName() + "." + method.getName();
                        mappedStatements.put(statement, ((Select) annotation).value());
                    }
                }

            }
        }
    }

    /**
     * 根据全局配置文件的Mapper接口路径，扫描所有接口
     *
     * @param mapperPath
     */
    private void scanPackage(String mapperPath) {
        String classPath = TestMybatis.class.getResource("/").getPath();
        mapperPath = mapperPath.replaceAll(".", File.separator);
        String mainPath = classPath + mapperPath;
        doPath(new File(mainPath));
        for (String className : classPaths) {
            className = className.replace(classPath.replace("/", "\\").replaceFirst("\\\\", ""), "").replace("\\", ".").replace(".class", "");
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (clazz.isInterface()) {
                mapperList.add(clazz);
            }
        }
    }

    /**
     * 获取文件或文件夹下所有的类.class
     *
     * @param file
     */
    private void doPath(File file) {
        // 文件夹，遍历
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File i : files) {
                doPath(i);
            }
        } else {
            // 文件，直接添加
            if (file.getName().endsWith(".class")) {
                classPaths.add(file.getPath());
            }
        }
    }

    /**
     * 创建执行器，当开启缓存时使用缓存装饰
     * 当配置插件时，使用插件代理
     *
     * @return
     */
    public Executor newExecutor() {
        Executor executor = null;
        if (properties.getString("cache.enabled").equals("true")) {
            executor = new CacheExecutor(new SimpleExecutor());
        } else {
            executor = new SimpleExecutor();
        }

        // 目前只拦截了Executor，所有的插件都对Executor进行代理，没有对拦截类和方法签名进行判断
        if (interceptorChain.hasPlugin()) {
            return (Executor) interceptorChain.pluginAll(executor);
        }

        return executor;

    }

    public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {

        return MAPPER_REGISTRY.getMapper(clazz, sqlSession);
    }

    /**
     * 根据statement判断是否存在映射的SQL
     *
     * @param statementId
     * @return
     */
    public boolean hasStatement(String statementId) {
        return mappedStatements.containsKey(statementId);
    }

    /**
     * 根据statementID获取SQL
     *
     * @param statementId
     * @return
     */
    public String getMappedStatement(String statementId) {
        return mappedStatements.get(statementId);
    }
}
