package io.github.mokeeqian.router.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouterStrategy {
    /**
     * 是否分表
     *
     * @return
     */
    boolean splitTable() default false;
}
