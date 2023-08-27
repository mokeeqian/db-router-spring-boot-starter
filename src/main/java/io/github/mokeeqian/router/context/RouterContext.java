package io.github.mokeeqian.router.context;

/**
 * @description: 路由上下文
 * @author：mokeeqian
 * @date: 2023/8/27
 * @Copyright： mokeeqian@gmail.com
 */
public class RouterContext {
    private static final ThreadLocal<String> DATABASE_KEY = new ThreadLocal<>();
    private static final ThreadLocal<String> TABLE_KEY = new ThreadLocal<>();

    public static String getDatabaseKey() {
        return DATABASE_KEY.get();
    }

    public static void setDatabaseKey(String dbKey) {
        DATABASE_KEY.set(dbKey);
    }

    public static String getTableKey() {
        return TABLE_KEY.get();
    }

    public static void setTableKey(String tbKey) {
        DATABASE_KEY.set(tbKey);
    }

    public static void clearDatabaseKey() {
        DATABASE_KEY.remove();
    }

    public static void clearTableKey() {
        TABLE_KEY.remove();
    }
}
