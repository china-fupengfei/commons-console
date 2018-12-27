package code.ponfee.job;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import code.ponfee.job.util.PageMethodArgumentResolver;

/**
 * SpringBoot启动类
 *
 * @author Ponfee
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    RedisAutoConfiguration.class
})
@ComponentScans({ // If not set, default current class package and posterity
    @ComponentScan("code.ponfee.console"),
    @ComponentScan("code.ponfee.job")
})
@ImportResource({"classpath:spring/spring-bean.xml"})
@EnableCaching
public class ApplicationStartup extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStartup.class, args);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new PageMethodArgumentResolver());
    }

}
