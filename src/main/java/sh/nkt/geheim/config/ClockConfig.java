package sh.nkt.geheim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {
    // this config is scanned and registered by Spring Boot

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
