package ge.itstep.demo.dto.game;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class rematchDTO {
    @NotEmpty()
    @Getter
    @Setter
    private String roomId;
    @NotEmpty()
    @Getter
    @Setter
    private boolean rematch;
    @NotEmpty()
    @Getter
    @Setter
    private boolean opponent;
    @NotEmpty()
    @Getter
    @Setter
    private String[][] board;
    @NotEmpty()
    @Getter
    @Setter
    private String player;

}
