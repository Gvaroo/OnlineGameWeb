package ge.itstep.demo.dto.game;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class createRoomDTO {
    @NotEmpty()
    @Getter
    @Setter
    private int size;
    @NotEmpty()
    @Getter
    @Setter
    private String[][] gameBoard;
}
