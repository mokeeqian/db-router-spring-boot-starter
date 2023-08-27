package io.github.mokeeqian.router.dynamic;

import io.github.mokeeqian.router.context.RouterContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @description: 动态数据源
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        // db01, db02, ...
        return "db" + RouterContext.getDatabaseKey();
    }
}
