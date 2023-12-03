package ge.itstep.demo.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class rejoinRoomDTO {
    @Getter
    @Setter
    private String role;
    @Getter
    @Setter
    private String uuid;

}
