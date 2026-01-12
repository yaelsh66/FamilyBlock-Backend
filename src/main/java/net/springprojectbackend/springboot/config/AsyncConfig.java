package net.springprojectbackend.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * A fixed-size thread pool used for Gemini calls.
     * Tune core/max size based on your needs and Gemini rate limits.
     */
    @Bean(name = "geminiExecutor")
    public Executor geminiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // up to 5 concurrent Gemini calls
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);        // additional tasks wait here
        executor.setThreadNamePrefix("gemini-");
        executor.initialize();
        return executor;
    }
}
