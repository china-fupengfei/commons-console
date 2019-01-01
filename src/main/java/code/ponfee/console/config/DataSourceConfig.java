package code.ponfee.console.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.druid.pool.DruidDataSource;

import code.ponfee.commons.data.MultipleDataSource;
import code.ponfee.commons.data.NamedDataSource;
import code.ponfee.commons.math.Numbers;

/**
 * Druid监控配置
 * 
 * http://localhost:8200/druid/login.html
 *
 * @author Ponfee
 */
@Configuration
public class DataSourceConfig {

    private static final String  PREFIX = "spring.datasource";
    private static final Pattern PATTERN = Pattern.compile("^(\\w+)\\.url$");

    @SuppressWarnings("unchecked")
    @Bean("jdbcConfig")
    public Properties jdbcConfig() throws IOException {
        PropertySourcesLoader propertiesLoader = new PropertySourcesLoader();
        PropertySource<?> source = propertiesLoader.load(
            new ClassPathResource("application-jdbc.yml")
        );
        Properties jdbcConfig = new Properties();
        ((Map<String, String>) source.getSource()).entrySet().stream().filter(
            e -> Objects.nonNull(e.getKey()) && e.getKey().startsWith(PREFIX)
        ).forEach(
            e -> jdbcConfig.put(e.getKey().substring(PREFIX.length() + 1), e.getValue())
        );
        /*PropertiesLoaderUtils.fillProperties(
            jdbcConfig, 
            new EncodedResource(new ClassPathResource("application-jdbc.yml"))
        );*/
        return jdbcConfig;
    }

    /**
     * 配置读取spring数据源
     */
    @Bean
    public MultipleDataSource dataSource(@Qualifier("jdbcConfig") Properties props) {
        List<String> names = props.keySet().stream().map(
            key -> {
                Matcher matcher = PATTERN.matcher(key.toString());
                return matcher.find() ? matcher.group(1) : null;
            }
        ).filter(
            Objects::nonNull
        ).collect(
            Collectors.toList()
        );

        String defaultName = props.getProperty("default", names.get(0));

        NamedDataSource defaultDataSource = null;
        List<NamedDataSource> dataSources = new ArrayList<>();
        for (String name : names) {
            DruidDataSource ds = new DruidDataSource();
            ds.setUrl(props.getProperty(name + ".url"));
            ds.setUsername(props.getProperty(name + ".username"));
            ds.setPassword(props.getProperty(name + ".password"));
            dataSourceSetting(props, ds);
            NamedDataSource namedDataSource = new NamedDataSource(name, ds);
            if (defaultName.equals(name)) {
                defaultDataSource = namedDataSource;
            } else {
                dataSources.add(namedDataSource);
            }
        }
        dataSources.add(0, defaultDataSource);
        return new MultipleDataSource(
            dataSources.toArray(new NamedDataSource[dataSources.size()])
        );
    }

    private void dataSourceSetting(Properties props, DruidDataSource ds) {
        ds.setDriverClassName(props.getProperty("driver-class-name"));
        //ds.setDbType(props.getProperty("type"));
        ds.setMaxActive((Integer)props.get("maxActive"));
        ds.setInitialSize((Integer)props.get("initialSize"));
        ds.setMinIdle((Integer)props.get("minIdle"));
        ds.setMaxWait((Integer)props.get("maxWait"));
        ds.setTimeBetweenEvictionRunsMillis((Integer)props.get("timeBetweenEvictionRunsMillis"));
        ds.setMinEvictableIdleTimeMillis((Integer)props.get("minEvictableIdleTimeMillis"));
        ds.setValidationQuery(props.getProperty("validationQuery"));
        ds.setTestWhileIdle(Numbers.toWrapBoolean(props.get("testWhileIdle"), false));
        ds.setTestOnBorrow(Numbers.toWrapBoolean(props.get("testOnBorrow"), false));
        ds.setTestOnReturn(Numbers.toWrapBoolean(props.get("testOnReturn"), false));
        ds.setPoolPreparedStatements(Numbers.toWrapBoolean(props.get("poolPreparedStatements"), false));
        ds.setMaxOpenPreparedStatements((Integer)props.get("maxOpenPreparedStatements"));
        try {
            ds.setFilters(props.getProperty("filters"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ds.setConnectionProperties(props.getProperty("connectionProperties"));
    }

}
