package da_figma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // =========================
            // AUTORIZZAZIONI
            // =========================
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/services",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/aboutus"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // =========================
            // LOGIN KEYCLOAK (OAUTH2)
            // =========================
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/", true)
            )

            // =========================
            // LOGOUT
            // =========================
            .logout(logout -> logout
            .logoutSuccessHandler((request, response, authentication) -> {

                String keycloakLogoutUrl =
                        "http://localhost:8180/realms/dafigma/protocol/openid-connect/logout"
                        + "?redirect_uri=http://localhost:8080/";

                response.sendRedirect(keycloakLogoutUrl);
            })
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")
        );

        return http.build();
    }
}