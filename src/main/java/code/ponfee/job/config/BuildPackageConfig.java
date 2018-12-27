package code.ponfee.job.config;

import code.ponfee.job.ApplicationStartup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Build package
 *
 * @author Ponfee
 */
@ConditionalOnProperty(name = "package.type", havingValue = "war")
public class BuildPackageConfig extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApplicationStartup.class);
    }
}
