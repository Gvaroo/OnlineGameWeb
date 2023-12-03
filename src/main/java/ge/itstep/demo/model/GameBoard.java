package ge.itstep.demo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@Entity
@Table(name = "game_board")
@NoArgsConstructor
@AllArgsConstructor
public class GameBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_id")
    @Getter
    @Setter
    private Rooms room;
    @Lob
    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String value;
    @Getter
    @Setter
    private String currentSymbol;
    @Getter
    @Setter
    private String currentTurn;

    public String[][] getArrayValue() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, String[][].class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to array", e);
        }
    }

    public void setArrayValue(String[][] arrayValue) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.value = objectMapper.writeValueAsString(arrayValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting array to JSON", e);
        }
    }
}
