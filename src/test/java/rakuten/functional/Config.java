package rakuten.functional;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import rakuten.clients.fixer.FixerClient;

@Profile("test")
@Configuration
public class Config {
    @Bean
    @Primary
    public FixerClient nameService() {
        return Mockito.mock(FixerClient.class);
    }
}