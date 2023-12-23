import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class QueryServiceEntryPoint {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(QueryServiceEntryPoint.class, args);
    }
}
