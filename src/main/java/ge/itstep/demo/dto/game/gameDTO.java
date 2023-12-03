package ge.itstep.demo.dto.game;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@AllArgsConstructor
public class gameDTO {
    @NotEmpty()
    @Getter
    @Setter
    private char[][] board;
    @NotEmpty()
    @Getter
    @Setter
    private char playerSymbol;

    @Getter
    @Setter
    private Integer row;

    @Getter
    @Setter
    private Integer col;
    @NotEmpty()
    @Getter
    @Setter
    private String roomUuid;
    @Getter
    @Setter
    @Nullable
    private boolean opponentLeft;
}
