package io.github.mokeeqian.router.aop;

import io.github.mokeeqian.router.annotation.DBRouter;
import io.github.mokeeqian.router.model.RouterConfig;
import io.github.mokeeqian.router.strategy.IRouterStrategy;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @description: 路由切面
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
@Aspect
public class RouterAspect {

    private static final Logger logger = LoggerFactory.getLogger(RouterAspect.class);

    private RouterConfig routerConfig;

    private IRouterStrategy routerStrategy;

    /**
     * 切点，拦截 @DBRouter
     */
    @Pointcut("@annotation(io.github.mokeeqian.router.annotation.DBRouter)")
    public void pointCut() {
    }

    /**
     * 切面环绕通知
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointCut() && @annotation(dbRouter)")
    public Object aroundAnnotationDBRouter(ProceedingJoinPoint proceedingJoinPoint, DBRouter dbRouter) throws Throwable {
        // 从 @DBRouter 注解中拿到路由 Key
        String dbKey = dbRouter.key();
        if (StringUtils.isBlank(dbKey) || StringUtils.isBlank(routerConfig.getRouterKey())) {
            throw new RuntimeException("annotation @DBRouter key is null");
        }

        // 如果 @DBRouter key 属性未指定，则默认使用 application.yml 中的 routerKey
        if (StringUtils.isBlank(dbKey)) {
            dbKey = routerConfig.getRouterKey();
        }

        // 获取路由字段的属性值
        String dbKeyFieldValue = parseRouterKeyFieldValue(dbKey, proceedingJoinPoint.getArgs());

        // 路由下发
        routerStrategy.doRouter(dbKeyFieldValue);

        // 放行
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            // 清空路由
            routerStrategy.clear();
        }
    }

    /**
     * 解析 routerKey(字段名) 对应的 字段属性值
     *
     * @param fieldName
     * @param args
     * @return
     */
    private String parseRouterKeyFieldValue(String fieldName, Object[] args) {
        if (args.length == 1) {
            Object arg = args[0];
            if (arg instanceof String) {
                return arg.toString();
            }
        }
        String fieldValue = null;
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(fieldValue)) {
                    break;
                }
                fieldValue = BeanUtils.getProperty(arg, fieldName);
            } catch (Exception e) {
                logger.error("获取路由字段属性值失败, key: {}, exception: {}", fieldName, e);
            }
        }
        return fieldValue;
    }
}
