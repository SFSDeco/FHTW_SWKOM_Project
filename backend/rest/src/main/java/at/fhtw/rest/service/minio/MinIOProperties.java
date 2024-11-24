package at.fhtw.rest.service.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinIOProperties {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucketName;


}
