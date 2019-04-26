package newenergy.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by HUST Corey on 2019-04-23.
 */
@Component
@ConfigurationProperties(prefix = "corey")
public class ServerConfig {
    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
