package ge.itstep.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;

@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    @Getter
    @Setter
    @NotEmpty()
    private String FullName;
    @Getter
    @Setter
    @NotEmpty()
    private String Email;
    @Getter
    @Setter
    @NotEmpty()
    private String Password;
}
