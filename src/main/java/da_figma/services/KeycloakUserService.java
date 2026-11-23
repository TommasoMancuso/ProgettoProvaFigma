package da_figma.services;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import da_figma.dtos.UserBookingDto;

@Service
public class KeycloakUserService {

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    public KeycloakUserService(OAuth2AuthorizedClientManager authorizedClientManager) {

        this.authorizedClientManager = authorizedClientManager;
    }

    public UserBookingDto getUserById(String keycloakId) {

        OAuth2AuthorizeRequest authorizeRequest =
                OAuth2AuthorizeRequest
                        .withClientRegistrationId("keycloak-admin")
                        .principal("dafigma-admin")
                        .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

        if (client == null) {
            throw new RuntimeException("Impossibile ottenere il token admin di Keycloak.");
        }

        String accessToken = client.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId;

        Map<String, Object> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Map.class)
                        .getBody();

        if (response == null) {
            throw new RuntimeException("Utente Keycloak non trovato.");
        }

        String firstName = (String) response.get("firstName");
        String lastName = (String) response.get("lastName");

        String username = (String) response.get("username");
        String email = (String) response.get("email");

        Map<String, Object> attributes = (Map<String, Object>) response.get("attributes");

        String phone = null;
        LocalDate birthDate = null;

        if (attributes != null) {

            if (attributes.containsKey("phone")) {
                phone = ((java.util.List<String>) attributes.get("phone")).get(0);
            }

            if (attributes.containsKey("birthDate")) {
                birthDate = LocalDate.parse(
                        ((java.util.List<String>) attributes.get("birthDate")).get(0));
            }
        }

        UserBookingDto dto = new UserBookingDto();
        dto.setKeycloakId(keycloakId);
        dto.setUsername(username);
        dto.setName(firstName);
        dto.setSurname(lastName);
        dto.setFullName(firstName + " " + lastName);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setBirthDate(birthDate);
        
        return dto;
    }
}