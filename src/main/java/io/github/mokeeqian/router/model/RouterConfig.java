package io.github.mokeeqian.router.model;

/**
 * @description: 路由配置
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
public class RouterConfig {
    /**
     * 分库数目
     */
    private int databaseCount;

    /**
     * 分表数目
     */
    private int tableCount;

    /**
     * 路由 key
     */
    private String routerKey;

    public RouterConfig() {
    }

    public RouterConfig(int databaseCount, int tableCount, String routerKey) {
        this.databaseCount = databaseCount;
        this.tableCount = tableCount;
        this.routerKey = routerKey;
    }

    public int getDatabaseCount() {
        return databaseCount;
    }

    public void setDatabaseCount(int databaseCount) {
        this.databaseCount = databaseCount;
    }

    public int getTableCount() {
        return tableCount;
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    public String getRouterKey() {
        return routerKey;
    }

    public void setRouterKey(String routerKey) {
        this.routerKey = routerKey;
    }
}
