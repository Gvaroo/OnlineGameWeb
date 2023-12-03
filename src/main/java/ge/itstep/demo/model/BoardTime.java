package ge.itstep.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_time")
@NoArgsConstructor
@AllArgsConstructor
public class BoardTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String player;
    @Getter
    @Setter
    private int timeLeft;
    @OneToOne
    @JoinColumn(name = "room_id")
    @Getter
    @Setter
    private Rooms room;
    @Column(name = "saved_date_time")
    @Getter
    @Setter
    private LocalDateTime savedDateTime = LocalDateTime.now();

}
