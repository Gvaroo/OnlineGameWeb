package ge.itstep.demo.repository;

import ge.itstep.demo.model.BoardTime;
import ge.itstep.demo.model.GameBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTimeRepository extends JpaRepository<BoardTime,Long> {
    BoardTime findBoardTimeByRoomId(Long roomId);
}
