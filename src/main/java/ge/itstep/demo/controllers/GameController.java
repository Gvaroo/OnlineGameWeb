package ge.itstep.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import ge.itstep.demo.dto.game.*;
import ge.itstep.demo.interfaces.IGameService;
import ge.itstep.demo.model.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    private final IGameService GameService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(IGameService GameService, SimpMessagingTemplate messagingTemplate) {
        this.GameService = GameService;
        this.messagingTemplate = messagingTemplate;
    }

//    @PostMapping("/game")
@PostMapping("/game")
    public ResponseEntity<ServiceResponse<checkWinDTO>> makeMove(@RequestBody gameDTO game) throws JsonProcessingException {
    ServiceResponse<checkWinDTO> result = GameService.makeMove(game);
    return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/createRoom")
    public ResponseEntity<ServiceResponse<createOrJoinRoomDTO>> createRoom(@RequestBody createRoomDTO createRoom) {
        try {
           ServiceResponse<createOrJoinRoomDTO> result = GameService.createGameRoom(createRoom);
            return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/joinRoom")
    public ResponseEntity<ServiceResponse<createOrJoinRoomDTO>> joinRoom(@RequestParam  Long roomId) {
        ServiceResponse<createOrJoinRoomDTO> result = GameService.joinGameRoom(roomId);
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
    @GetMapping("/getRoom")
    public ResponseEntity<ServiceResponse<roomDTO>> getRoom(@RequestParam  String roomUuid,@RequestParam String ur) {
        ServiceResponse<roomDTO> result = GameService.getGameRoom(roomUuid,ur);
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
    @GetMapping("/getRooms")
    public ResponseEntity<ServiceResponse<List<roomDTO>>> getRooms() {
        ServiceResponse<List<roomDTO>> result = GameService.getGameRooms();
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/userLeft")
    public ResponseEntity<ServiceResponse<userLeftDTO>> userLeftGameBoard(
            @RequestPart("player") String player,
            @RequestPart("roomUuid") String roomUuid,
            @RequestPart("timeLeft") String timeLeft) {
        userLeftDTO dto = new userLeftDTO();
        int time = Integer.parseInt(timeLeft);
        dto.setPlayer(player);
        dto.setRoomUuid(roomUuid);
        dto.setTimeLeft(time);
        ServiceResponse<userLeftDTO> result = GameService.userLeftBoard(dto);
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
    @MessageMapping("/topic/{roomId}/rematchRequest")
    public void handleRematchRequest(@Payload rematchDTO rematchDTO,@DestinationVariable String roomId) {


        messagingTemplate.convertAndSend("/topic/" + roomId  + "/requestState", rematchDTO);
    }
    @PostMapping("/rematchAccepted")
    public ResponseEntity<ServiceResponse<rematchDTO>> rematchAccepted(@RequestBody rematchDTO dto) {
        ServiceResponse<rematchDTO> result = GameService.rematchAccepted(dto);
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
    @GetMapping("/findUserRooms")
    public ResponseEntity<ServiceResponse<List<roomDTO>>> getUserRooms() {
        ServiceResponse<List<roomDTO>> result = GameService.getUserGameRooms();
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/reconnectRoom")
    public ResponseEntity<ServiceResponse<rejoinRoomDTO>> rejoinRoom(@RequestParam Long roomId) throws JsonProcessingException {
        ServiceResponse<rejoinRoomDTO> result = GameService.reconnectToRoom(roomId);
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
    @GetMapping("/closeRoom")
    public boolean closeRoom(@RequestParam String uuid) {
        return this.GameService.closeRoom(uuid);
    }
}
