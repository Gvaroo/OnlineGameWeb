package ge.itstep.demo.repository;

import ge.itstep.demo.model.Rooms;
import ge.itstep.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Rooms,Long> {
    Rooms findRoomsById(Long id);
    Rooms findRoomsByUuid(UUID uuid);
    List<Rooms> findRoomsByRoomOwnerOrOpponent(User user,User user2);

}
