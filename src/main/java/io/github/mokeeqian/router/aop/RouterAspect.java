package io.github.mokeeqian.router.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @description: 路由切面
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
@Aspect
public class RouterAspect {

    @Pointcut("@annotation(DoRouter)")
    public void pointCut(){}

    @Around("pointCut()")
    public Object doRouter(ProceedingJoinPoint proceedingJoinPoint) {

    }
}
