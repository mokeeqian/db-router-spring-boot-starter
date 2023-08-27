package io.github.mokeeqian.router.config;

import io.github.mokeeqian.router.aop.RouterAspect;
import io.github.mokeeqian.router.dynamic.DynamicDataSource;
import io.github.mokeeqian.router.dynamic.DynamicMybatisPlugin;
import io.github.mokeeqian.router.model.RouterConfig;
import io.github.mokeeqian.router.strategy.IRouterStrategy;
import io.github.mokeeqian.router.strategy.impl.RouterStrategyHashCode;
import io.github.mokeeqian.router.util.PropertyUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description: 数据源配置
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    /**
     * 数据源
     * db -> Config
     */
    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();

    /**
     * 默认数据源配置
     */
    private Map<String, Object> defaultDataSourceConfig;

    /**
     * database 数目
     */
    private int databaseCount;

    /**
     * table 数目
     */
    private int tableCount;

    /**
     * 路由 key
     */
    private String routerKey;

    @Bean
    public RouterConfig routerConfig() {
        return new RouterConfig(this.databaseCount, this.tableCount, this.routerKey);
    }

    @Bean
    public IRouterStrategy routerStrategy(RouterConfig routerConfig) {
        return new RouterStrategyHashCode(routerConfig);
    }

    @Bean
    public Interceptor plugin() {
        return new DynamicMybatisPlugin();
    }

    @Bean(name = "routerAspect")
    @ConditionalOnMissingBean
    public RouterAspect routerAspect(RouterConfig routerConfig, IRouterStrategy routerStrategy) {
        return new RouterAspect(routerConfig, routerStrategy);
    }

    /**
     * 这个数据源就会被 MyBatis SpringBoot Starter 中 SqlSessionFactory sqlSessionFactory(DataSource dataSource) 注入使用
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String dbInfo : dataSourceMap.keySet()) {
            Map<String, Object> objMap = dataSourceMap.get(dbInfo);
            targetDataSources.put(dbInfo, new DriverManagerDataSource(
                    objMap.get("url").toString(), objMap.get("username").toString(), objMap.get("password").toString())
            );
        }

        // 设置数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(
                new DriverManagerDataSource(
                        defaultDataSourceConfig.get("url").toString(),
                        defaultDataSourceConfig.get("username").toString(),
                        defaultDataSourceConfig.get("password").toString()
                )
        );
        return dynamicDataSource;
    }

    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }

    /**
     * 读取 yml 数据源配置
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        String prefix = "mini-db-router.jdbc.datasource.";

        databaseCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "dbCount")));
        tableCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "tbCount")));
        routerKey = environment.getProperty(prefix + "routerKey");

        // 分库列表 db01,db02
        String dataSources = environment.getProperty(prefix + "list");
        assert dataSources != null;
        for (String dbInfo : dataSources.split(",")) {
            Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
            dataSourceMap.put(dbInfo, dataSourceProps);
        }

        // 默认数据源
        String defaultData = environment.getProperty(prefix + "default");
        defaultDataSourceConfig = PropertyUtil.handle(environment, prefix + defaultData, Map.class);
    }
}
