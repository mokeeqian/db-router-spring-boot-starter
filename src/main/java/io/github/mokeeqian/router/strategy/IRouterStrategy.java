package io.github.mokeeqian.router.strategy;

/**
 * @description: 路由策略接口，后续接入新的路由策略，实现该接口即可
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
public interface IRouterStrategy {
    /**
     * 执行路由
     *
     * @param databaseKeyFieldValue 路由key字段属性值
     */
    void doRouter(String databaseKeyFieldValue);

    /**
     * 以下方法是抽离出来的接口逻辑，供：主动路由、全库全表扫描等场景使用
     */

    /**
     * 设置 database key
     *
     * @param databaseIndex 计算出来的 database 索引
     */
    void setDatabaseKey(int databaseIndex);

    /**
     * 设置 table key
     *
     * @param tableIndex 计算出来的 table 索引
     */
    void setTableKey(int tableIndex);

    /**
     * 清空路由
     */
    void clear();

    /**
     * 返回 database 数目
     *
     * @return
     */
    int databaseCount();

    /**
     * 返回 table 数目
     *
     * @return
     */
    int tableCount();
}
