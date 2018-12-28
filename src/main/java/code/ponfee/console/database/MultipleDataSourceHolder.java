package code.ponfee.console.database;

/**
 * Multiple DataSource
 * 
 * @author Ponfee
 */
public final class MultipleDataSourceHolder {

    private MultipleDataSourceHolder() {}

    public final static String DEFAULT_DATASOURCE_NAME = "default";

    private static final ThreadLocal<String> DATASOURCE_NAME_HOLDER = new ThreadLocal<>();

    public static void set(String datasourceName) {
        DATASOURCE_NAME_HOLDER.set(datasourceName);
    }

    public static String get() {
        return DATASOURCE_NAME_HOLDER.get();
    }

    public static void remove() {
        DATASOURCE_NAME_HOLDER.remove();
    }
}
