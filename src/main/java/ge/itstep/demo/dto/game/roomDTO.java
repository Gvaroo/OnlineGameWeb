package ge.itstep.demo.dto.game;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class roomDTO {
    @Getter
    @Setter
    private Long roomId;
    @Getter
    @Setter
    private String Uuid;
    @Getter
    @Setter
    private String ownerUsername;
    @Getter
    @Setter
    private String opponentUsername;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private int size;
    @Getter
    @Setter
    private String[][] board;
    @Getter
    @Setter
    private String currentSymbol;
    @Getter
    @Setter
    private String currentTurn;
    @Getter
    @Setter
    private Long timeLeft;
}
