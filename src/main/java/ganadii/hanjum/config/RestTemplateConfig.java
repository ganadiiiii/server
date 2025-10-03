package ganadii.hanjum.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate with timeout configuration for external API calls
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5 seconds connection timeout
        factory.setReadTimeout(30000);    // 30 seconds read timeout

        return new RestTemplate(factory);
    }
}
