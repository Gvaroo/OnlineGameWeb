package ge.itstep.demo.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import ge.itstep.demo.dto.LoginUserDTO;
import ge.itstep.demo.dto.game.*;
import ge.itstep.demo.model.Rooms;
import ge.itstep.demo.model.ServiceResponse;

import java.security.Provider;
import java.util.List;

public interface IGameService {
    ServiceResponse<checkWinDTO> makeMove(gameDTO gameBoard) throws JsonProcessingException;
    boolean checkWin(gameDTO game,int size) throws JsonProcessingException;
    ServiceResponse<createOrJoinRoomDTO> createGameRoom(createRoomDTO createRoom) throws JsonProcessingException;
    ServiceResponse<List<roomDTO>> getGameRooms();
    ServiceResponse<List<roomDTO>> getUserGameRooms();
    ServiceResponse<roomDTO> getGameRoom(String gameUuid,String ur);
    ServiceResponse<createOrJoinRoomDTO> joinGameRoom(Long gameRoom);
    ServiceResponse<userLeftDTO> userLeftBoard(userLeftDTO dto);
    ServiceResponse<rematchDTO> rematchAccepted(rematchDTO dto);
    boolean closeRoom(String uuid);
    ServiceResponse<rejoinRoomDTO> reconnectToRoom(Long roomId) throws JsonProcessingException;
    ServiceResponse<List<rankingDTO>> getLeaderboard();

}
