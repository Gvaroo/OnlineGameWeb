package ge.itstep.demo.dto;

import ge.itstep.demo.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    @NotEmpty()
    @Getter
    @Setter
    private String name;
    @NotEmpty()
    @Email
    @Getter
    @Setter
    private String email;



    public UserInfoDTO(User user)
    {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
