package ge.itstep.demo.dto.game;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@AllArgsConstructor
public class checkWinDTO {
    @NotEmpty()
    @Getter
    @Setter
    private char playerSymbol;
    @Nullable
    @Getter
    @Setter
    private Integer row;
    @Nullable
    @Getter
    @Setter
    private Integer col;
    @Getter
    @Setter
    private boolean gameOver;
    @Getter
    @Setter
    @NotEmpty()
    private String currentPlayer;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private boolean winner;
    @Getter
    @Setter
    private boolean draw;
    @Getter
    @Setter
    @Nullable
    private boolean opponentLeft;
    @Getter
    @Setter
    private String[][] board;

}
