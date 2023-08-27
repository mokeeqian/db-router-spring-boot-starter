package io.github.mokeeqian.router.strategy.impl;

import io.github.mokeeqian.router.context.RouterContext;
import io.github.mokeeqian.router.model.RouterConfig;
import io.github.mokeeqian.router.strategy.IRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: hash 散列 路由策略
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
public class RouterStrategyHashCode implements IRouterStrategy {

    private static final Logger logger = LoggerFactory.getLogger(RouterStrategyHashCode.class);

    private RouterConfig routerConfig;

    public RouterStrategyHashCode(RouterConfig routerConfig) {
        this.routerConfig = routerConfig;
    }

    @Override
    public void doRouter(String databaseKeyFieldValue) {
        // 总的库表数目
        int size = routerConfig.getDatabaseCount() * routerConfig.getTableCount();

        // 扰动函数；在 JDK 的 HashMap 中，对于一个元素的存放，需要进行哈希散列。而为了让散列更加均匀，所以添加了扰动函数。
        int idx = (size - 1) & (databaseKeyFieldValue.hashCode() ^ (databaseKeyFieldValue.hashCode() >>> 16));

        // 库表索引；相当于是把一个长条的桶，切割成段，对应分库分表中的库编号和表编号
        int dbIdx = idx / routerConfig.getTableCount() + 1;
        int tbIdx = idx - routerConfig.getTableCount() * (dbIdx - 1);

        // 设置路由到 context
        RouterContext.setDatabaseKey(String.format("%02d", dbIdx));
        RouterContext.setTableKey(String.format("%03d", tbIdx));
    }

    @Override
    public void setDatabaseKey(int databaseIndex) {
        // db_00, db_01, ...
        RouterContext.setDatabaseKey(String.format("%02d", databaseIndex));
    }

    @Override
    public void setTableKey(int tableIndex) {
        // tb_001, tb_002, ...
        RouterContext.setTableKey(String.format("%03d", tableIndex));
    }

    @Override
    public void clear() {
        RouterContext.clearDatabaseKey();
        RouterContext.clearTableKey();
    }

    @Override
    public int databaseCount() {
        return routerConfig.getDatabaseCount();
    }

    @Override
    public int tableCount() {
        return routerConfig.getTableCount();
    }
}
