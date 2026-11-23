package da_figma.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBookingDto {

    // =========================
    // KEYCLOAK ID
    // =========================

    private String keycloakId;

    // =========================
    // USERNAME
    // =========================

    private String username;

    // =========================
    // PERSONAL INFO
    // =========================

    private String name;

    private String surname;

    private String fullName;

    private LocalDate birthDate;

    // =========================
    // CONTACTS
    // =========================

    private String email;

    private String phone;

}