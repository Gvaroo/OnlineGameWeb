package ge.itstep.demo.dto.game;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class userLeftDTO {
    @Getter
    @Setter
    @NotEmpty()
    private String player;
    @Getter
    @Setter
    private int timeLeft;
    @Getter
    @Setter
    @NotEmpty()
    private String roomUuid;
}
