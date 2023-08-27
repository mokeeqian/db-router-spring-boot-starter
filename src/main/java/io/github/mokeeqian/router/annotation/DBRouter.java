package io.github.mokeeqian.router.annotation;

import java.lang.annotation.*;

/**
 * @description: 路由注解
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouter {

    /**
     * 路由字段
     *
     * @return
     */
    String key() default "";

}
