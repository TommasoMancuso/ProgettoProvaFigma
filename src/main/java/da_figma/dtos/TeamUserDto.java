package da_figma.dtos;

import java.time.LocalDate;

import da_figma.models.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUserDto {

    private Long id;
    private String name;
    private String surname;
    private String email;

    private String phone;
    private LocalDate birthDate;

    private String bio;
    private String profileImage;
    private String instagram;

    private TeamRole teamRole;

    public String getFullName() {
        return String.format("%s %s", name, surname).trim();
    }
}