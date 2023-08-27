package io.github.mokeeqian.router.dynamic;

import io.github.mokeeqian.router.annotation.DBRouterStrategy;
import io.github.mokeeqian.router.context.RouterContext;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: Mybatis 拦截器，拦截&改写 SQL
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
@Intercepts(
        // 拦截 StatementHandler#prepare
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
        )
)
public class DynamicMybatisPlugin implements Interceptor {

    /**
     * 正则匹配 SQL 中所有的表 DML 操作
     */
    private final Pattern pattern = Pattern.compile("(from|into|update)[\\s]{1,}(\\w{1,})", Pattern.CASE_INSENSITIVE);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取 StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(
                statementHandler,
                SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                new DefaultReflectorFactory()
        );
        // 获取 MappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // 获取自定义注解，判断是否需要分表 splitTable = true
        // clazzName + methodName
        String id = mappedStatement.getId();
        String clazzName = id.substring(0, id.lastIndexOf("."));
        Class<?> clazz = Class.forName(clazzName);
        DBRouterStrategy dbRouterStrategy = clazz.getAnnotation(DBRouterStrategy.class);

        // 如果无需分表，直接返回
        if (dbRouterStrategy == null || !dbRouterStrategy.splitTable()) {
            return invocation.proceed();
        }

        // 获取 Mybatis 原始 SQL
        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSql = boundSql.getSql();

        // SQL 替换
        Matcher matcher = pattern.matcher(originalSql);
        String tableName = null;
        if (matcher.find()) {
            tableName = matcher.group().trim();
        }
        assert tableName != null;
        String replacedSql = matcher.replaceAll(tableName + "_" + RouterContext.getTableKey());

        // 反射修改SQL
        Field sqlField = boundSql.getSql().getClass().getDeclaredField("sql");
        sqlField.setAccessible(true);
        sqlField.set(boundSql, replacedSql);
        sqlField.setAccessible(false);

        return invocation.proceed();
    }
}
