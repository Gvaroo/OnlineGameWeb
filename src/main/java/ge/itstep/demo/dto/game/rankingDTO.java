package ge.itstep.demo.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class rankingDTO {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String fullName;
    @Getter
    @Setter
    private int matches;
    @Getter
    @Setter
    private int draws;
    @Getter
    @Setter
    private int loses;
    @Getter
    @Setter
    private double winningProcent;
    @Getter
    @Setter
    private int wins;
}
