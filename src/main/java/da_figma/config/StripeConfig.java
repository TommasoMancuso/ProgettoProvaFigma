package da_figma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class StripeConfig {

    @Value("${stripe.public-key}")
    private String publicKey;

    @Value("${stripe.secret-key}")
    private String secretKey;
}