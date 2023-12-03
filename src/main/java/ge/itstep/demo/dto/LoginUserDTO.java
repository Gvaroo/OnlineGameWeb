package ge.itstep.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDTO {
    @Getter
    @Setter
    @NotEmpty()
    private String Email;
    @Getter
    @Setter
    @NotEmpty()
    private String Password;
}
