package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
//@Import(value = {DefaultSecurityConfiguration.class})
public class QueryServiceConfig {
    private static final Logger logger = LoggerFactory.getLogger(QueryServiceConfig.class);
}
