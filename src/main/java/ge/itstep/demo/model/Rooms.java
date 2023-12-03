package ge.itstep.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@AllArgsConstructor
public class Rooms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private UUID uuid;
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "room_owner_id")
    private User roomOwner;
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "opponent_id")
    @Nullable
    private User opponent;

    @Getter
    @Setter
    private String status = "Waiting";
    @Getter
    @Setter
    private int size;
    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
    @JoinColumn(name = "gameBoard_id")
    @Getter
    @Setter
    private GameBoard gameBoard;
    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
    @JoinColumn(name = "boardTime_id")
    @Getter
    @Setter
    private BoardTime boardTimes;
    @Getter
    @Setter
    private LocalDateTime createdDateTime;
}
