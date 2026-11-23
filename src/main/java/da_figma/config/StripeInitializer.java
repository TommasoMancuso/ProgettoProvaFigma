package da_figma.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeInitializer {

    @Autowired
    private StripeConfig stripeConfig;

    @PostConstruct
    public void init() {

        Stripe.apiKey = stripeConfig.getSecretKey();
    }
}