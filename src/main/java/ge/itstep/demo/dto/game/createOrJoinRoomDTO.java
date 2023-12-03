package ge.itstep.demo.dto.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class createOrJoinRoomDTO {
    @NotEmpty()
    @Getter
    @Setter
    @JsonProperty("gameId")
    private String gameId;
    @NotEmpty()
    @Getter
    @Setter
    @JsonProperty("gameUid")
    private String uuid;
    @NotEmpty()
    @Getter
    @Setter
    @JsonProperty("role")
    private String role;
    @NotEmpty()
    @Getter
    @Setter
    @JsonProperty("userName")
    private String userName;
    @NotEmpty()
    @Getter
    @Setter
    @JsonProperty("size")
    private int size;


}
