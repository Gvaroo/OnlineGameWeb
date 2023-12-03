package ge.itstep.demo.repository;

import ge.itstep.demo.model.GameBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<GameBoard,Long> {
  GameBoard findGameBoardByRoom_Id(long id);

}
