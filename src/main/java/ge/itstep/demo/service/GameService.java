package ge.itstep.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ge.itstep.demo.dto.game.*;
import ge.itstep.demo.interfaces.IGameService;
import ge.itstep.demo.model.*;
import ge.itstep.demo.repository.BoardRepository;
import ge.itstep.demo.repository.BoardTimeRepository;
import ge.itstep.demo.repository.RoomRepository;
import ge.itstep.demo.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameService implements IGameService {
    private final TokenValidationService tokenValidationService;
    private final RoomRepository roomRepository;
    private final BoardRepository boardRepository;
    private final BoardTimeRepository boardTimeRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GameService(TokenValidationService tokenValidationService, RoomRepository roomRepository, BoardRepository boardRepository, BoardTimeRepository boardTimeRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.tokenValidationService = tokenValidationService;
        this.roomRepository = roomRepository;
        this.boardRepository = boardRepository;
        this.boardTimeRepository = boardTimeRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public ServiceResponse<checkWinDTO> makeMove(gameDTO game) throws JsonProcessingException {
        ServiceResponse<checkWinDTO> response = new ServiceResponse<checkWinDTO>();
        try {
            User user = tokenValidationService.getUserFromToken();
            String currentPlayer = "creator";
            if(user == null) {
                response.setSuccess(false);
                response.setMessage("user not found");
                return response;
            }
            var room = roomRepository.findRoomsByUuid(UUID.fromString(game.getRoomUuid()));
            if(room.getOpponent().getId().equals(user.getId()))
                currentPlayer = "oponnent";
            checkWinDTO dto = new checkWinDTO(game.getPlayerSymbol(), game.getRow(), game.getCol(), false,currentPlayer,user.getName(),false, false,false,null);
            if(game.getRow() != null)
            {
                if (checkWin(game,room.getSize())) {
                    User opponent = null;
                    dto.setWinner(true);
                    dto.setGameOver(true);
                    user.setWins(user.getWins() + 1);
                    if(currentPlayer.equals("creator")) {
                        opponent = userRepository.findUserById(room.getOpponent().getId());
                        opponent.setLoses(opponent.getLoses() +1);
                    }else {
                        opponent = userRepository.findUserById(room.getRoomOwner().getId());
                        opponent.setLoses(opponent.getLoses() + 1);
                    }
                    userRepository.saveAll(List.of(user, opponent));
                } else if(checkIfDraw(game.getBoard())){
                    var owner = room.getRoomOwner();
                    var opponent = room.getOpponent();
                    owner.setDraws(owner.getDraws()+1);
                    opponent.setDraws(opponent.getDraws()+1);
                    dto.setGameOver(true);
                    dto.setDraw(true);
                }
            }
            //Saving gameBoard State to database
            var gameBoard = boardRepository.findGameBoardByRoom_Id(room.getId());
            String opponent = currentPlayer.equals("creator") ? "oponnent" : "creator";
            BoardTime boardTime = room.getBoardTimes();
            if(game.isOpponentLeft() && game.getRow() ==null){

                if(boardTime !=null && boardTime.getPlayer().equals(opponent)) {
                    gameBoard.setCurrentTurn(currentPlayer);
                    boardTime.setSavedDateTime(LocalDateTime.now());
                }
                dto.setOpponentLeft(true);
            }else if(game.isOpponentLeft() && game.getRow() !=null){
                if(boardTime !=null && boardTime.getPlayer().equals(opponent)) {
                    gameBoard.setCurrentTurn(opponent);
                    boardTime.setSavedDateTime(LocalDateTime.now());
                }
                dto.setOpponentLeft(true);
            } else {
                gameBoard.setCurrentTurn(opponent);
            }
            gameBoard.setCurrentSymbol(game.getPlayerSymbol() == 'X' ? "O" : "X");
            dto.setCurrentPlayer(gameBoard.getCurrentTurn());
            dto.setPlayerSymbol(gameBoard.getCurrentSymbol().charAt(0));
            String[][] stringBoard = convertCharBoardToStringBoard(game.getBoard());
            gameBoard.setArrayValue(stringBoard);
            room.setGameBoard(gameBoard);
            gameBoard.setRoom(room);
            roomRepository.save(room);
            dto.setBoard(stringBoard);
            response.setData(dto);
            messagingTemplate.convertAndSend("/topic/" + game.getRoomUuid() + "/test", response);

        }catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }

        return response;
    }

    @Override
    public boolean checkWin(gameDTO game, int size) throws JsonProcessingException {

        char playerSymbol = game.getPlayerSymbol();
        char[][] board = game.getBoard();

        // Check horizontally
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (j + 4 < size &&
                        board[i][j] == playerSymbol &&
                        board[i][j + 1] == playerSymbol &&
                        board[i][j + 2] == playerSymbol &&
                        board[i][j + 3] == playerSymbol &&
                        board[i][j + 4] == playerSymbol) {
                    return true;
                }
            }
        }

        // Check vertically
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i + 4 < size &&
                        board[i][j] == playerSymbol &&
                        board[i + 1][j] == playerSymbol &&
                        board[i + 2][j] == playerSymbol &&
                        board[i + 3][j] == playerSymbol &&
                        board[i + 4][j] == playerSymbol) {
                    return true;
                }
            }
        }

        // Check diagonally (top-left to bottom-right)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i + 4 < size && j + 4 < size &&
                        board[i][j] == playerSymbol &&
                        board[i + 1][j + 1] == playerSymbol &&
                        board[i + 2][j + 2] == playerSymbol &&
                        board[i + 3][j + 3] == playerSymbol &&
                        board[i + 4][j + 4] == playerSymbol) {
                    return true;
                }
            }
        }

        // Check diagonally (top-right to bottom-left)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i + 4 < size && j - 4 >= 0 &&
                        board[i][j] == playerSymbol &&
                        board[i + 1][j - 1] == playerSymbol &&
                        board[i + 2][j - 2] == playerSymbol &&
                        board[i + 3][j - 3] == playerSymbol &&
                        board[i + 4][j - 4] == playerSymbol) {
                    return true;
                }
            }
        }

        return false;
    }




    @Override
    public ServiceResponse<createOrJoinRoomDTO> createGameRoom(createRoomDTO createRoom) throws JsonProcessingException {
     ServiceResponse<createOrJoinRoomDTO> response = new ServiceResponse<createOrJoinRoomDTO>();
        createOrJoinRoomDTO dto = null;
        try {
            User user = tokenValidationService.getUserFromToken();
            if (user == null) {
                response.setSuccess(false);
                response.setMessage("user is null");
                return response;
            }
            GameBoard gameboard = new GameBoard();
            gameboard.setArrayValue(createRoom.getGameBoard());
            Rooms room = new Rooms();
            room.setUuid(UUID.randomUUID());
            room.setRoomOwner(user);
            room.setSize(createRoom.getSize());
            room.setGameBoard(gameboard);

            gameboard.setRoom(room);

            roomRepository.save(room);
            dto = new createOrJoinRoomDTO(room.getId().toString(),room.getUuid().toString(), "creatorUser",user.getName(),room.getSize());
            response.setData(dto);

            this.messagingTemplate.convertAndSend("/topic/" + room.getUuid().toString() + "/gameState", dto);
            // Check if opponent is not null
            String opponentName = room.getOpponent() != null ? room.getOpponent().getName() : null;
            this.messagingTemplate.convertAndSend("/topic/roomsState", new roomDTO(room.getId(),room.getUuid().toString(),room.getRoomOwner().getName(),opponentName,room.getStatus(),room.getSize(),null,null,
                    null,
                    null));

        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }

      return response;

    }

    @Override
    public ServiceResponse<List<roomDTO>> getGameRooms() {
        ServiceResponse<List<roomDTO>> response = new ServiceResponse<List<roomDTO>>();
        try {
            var rooms = roomRepository.findAll();
            List<roomDTO> userRooms = rooms.stream()
                    .sorted(Comparator.comparing(Rooms::getId).reversed())
                    .map(c -> new roomDTO(
                            c.getId(),
                            c.getUuid().toString(),
                            c.getRoomOwner().getName(),
                            c.getOpponent() != null ? c.getOpponent().getName() : null,
                            c.getStatus(),
                            c.getSize(),
                            null,
                            null,
                            null,
                            null

                    ))

                    .collect(Collectors.toList());

            response.setData(userRooms);
        }
        catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    @Override
    public ServiceResponse<List<roomDTO>> getUserGameRooms() {
        ServiceResponse<List<roomDTO>> response = new ServiceResponse<List<roomDTO>>();
        try{
           var user = tokenValidationService.getUserFromToken();
           var rooms = roomRepository.findRoomsByRoomOwnerOrOpponent(user,user);
            List<roomDTO> userRooms = rooms.stream()
                    .sorted(Comparator.comparing(Rooms::getId).reversed())
                    .map(c -> new roomDTO(
                            c.getId(),
                            c.getUuid().toString(),
                            c.getRoomOwner().getName(),
                            c.getOpponent() != null ? c.getOpponent().getName() : null,
                            c.getStatus(),
                            c.getSize(),
                            null,
                            null,
                            null,
                            null

                    ))

                    .collect(Collectors.toList());

            response.setData(userRooms);
        }
        catch (Exception ex){
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    @Override
    public ServiceResponse<roomDTO> getGameRoom(String gameUuid,String ur) {
        ServiceResponse<roomDTO> response = new ServiceResponse<roomDTO>();
        try{
            var user = tokenValidationService.getUserFromToken();
            var room = roomRepository.findRoomsByUuid(UUID.fromString(gameUuid));
            var board = boardRepository.findGameBoardByRoom_Id(room.getId());
            BoardTime boardTime = room.getBoardTimes();

            if (!isUserAuthorized(user, room, ur)) {
                response.setSuccess(false);
                response.setMessage("You should not be here");
                return response;
            }


            String opponentName = room.getOpponent() != null ? room.getOpponent().getName() : null;

            var dto = new roomDTO(
                    room.getId(),
                    room.getUuid().toString(),
                    room.getRoomOwner().getName(),
                    opponentName,
                    room.getStatus(),
                    room.getSize(),
                    board.getArrayValue(),
                    board.getCurrentSymbol(),
                    board.getCurrentTurn(),
                    null
            );

            if(boardTime !=null) {
                if(boardTime.getPlayer().equals(board.getCurrentTurn())){
                    Long remainingSeconds = calculateTimeDifference(boardTime.getSavedDateTime());
                    if(remainingSeconds > 0)
                        dto.setTimeLeft(remainingSeconds);
                }
                this.boardTimeRepository.delete(boardTime);
            }

            response.setData(dto);
        }catch (Exception ex){
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    @Transactional
    @Override
    public ServiceResponse<createOrJoinRoomDTO> joinGameRoom(Long gameRoom) {
        ServiceResponse<createOrJoinRoomDTO> response = new ServiceResponse<createOrJoinRoomDTO>();
        createOrJoinRoomDTO dto = null;
        try {
            User user = tokenValidationService.getUserFromToken();
            if (user == null) {
                response.setSuccess(false);
                response.setMessage("user is null");
                return response;
            }
            Rooms room = roomRepository.findRoomsById(gameRoom);
            if(user.equals(room.getRoomOwner())){
                response.setSuccess(false);
                response.setMessage("You cant join your own room :)");
                return response;
            }
            if (room == null) {
                response.setSuccess(false);
                response.setMessage("Room doesnt exist!");
                return response;
            }
            room.setOpponent(user);
            room.setCreatedDateTime(LocalDateTime.now());
            room.setStatus("Playing");
            roomRepository.save(room);

            user.setPlayedMatches(user.getPlayedMatches() +1);
            User opponent = userRepository.findUserById(room.getRoomOwner().getId());
            opponent.setPlayedMatches(opponent.getPlayedMatches() +1);
            userRepository.saveAll(List.of(user,opponent));
            dto = new createOrJoinRoomDTO(room.getId().toString(),room.getUuid().toString(), "oponnentUser",user.getName(),room.getSize());
            response.setData(dto);
            this.messagingTemplate.convertAndSend("/topic/" + room.getUuid().toString() + "/gameState", dto);
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());

        }

        return response;
    }

    @Override
    public ServiceResponse<userLeftDTO> userLeftBoard(userLeftDTO dto) {
       ServiceResponse<userLeftDTO> response = new ServiceResponse<userLeftDTO>();
       try {
           var room = roomRepository.findRoomsByUuid(UUID.fromString(dto.getRoomUuid()));
           var boardTime = boardTimeRepository.findBoardTimeByRoomId(room.getId());
           var gameBoard = boardRepository.findGameBoardByRoom_Id(room.getId());
           gameBoard.setCurrentSymbol(dto.getPlayer() == "creator" ? "O" : "X");
           if(dto.getTimeLeft() > 0)
           gameBoard.setCurrentTurn(dto.getPlayer());
           else
           gameBoard.setCurrentTurn(dto.getPlayer() == "creator" ? "oponnent" : "creator");
           if(boardTime !=null) {
               boardTime.setTimeLeft(dto.getTimeLeft());

           } else{
               boardTime = new BoardTime();
               boardTime.setTimeLeft(dto.getTimeLeft());
           }
           boardTime.setPlayer(dto.getPlayer());
           boardTime.setSavedDateTime(LocalDateTime.now());
           room.setBoardTimes(boardTime);
           gameBoard.setRoom(room);
           boardTime.setRoom(room);
           roomRepository.save(room);
           response.setData(dto);
           this.messagingTemplate.convertAndSend("/topic/" + room.getUuid().toString() + "/userState/" + dto.getPlayer(), dto);
       }
       catch (Exception ex){
           response.setSuccess(false);
           response.setMessage(ex.getMessage());
       }
       return response;
    }

    @Override
    public ServiceResponse<rematchDTO> rematchAccepted(rematchDTO dto) {
        ServiceResponse<rematchDTO> response = new ServiceResponse<rematchDTO>();
        try {
         if(dto.isOpponent()){

             //reset gameboard
             var room = roomRepository.findRoomsByUuid(UUID.fromString(dto.getRoomId()));
             var gameBoard = boardRepository.findGameBoardByRoom_Id(room.getId());
             gameBoard.setArrayValue(dto.getBoard());
             gameBoard.setCurrentTurn("creator");
             gameBoard.setCurrentSymbol("X");
             room.setGameBoard(gameBoard);

             //reset room created time
             room.setCreatedDateTime(LocalDateTime.now());

             //set matches +1
             var creator = room.getRoomOwner();
             var opponent = room.getOpponent();
             creator.setPlayedMatches(creator.getPlayedMatches()+1);
             opponent.setPlayedMatches(opponent.getPlayedMatches()+1);
             roomRepository.save(room);
             userRepository.saveAll(List.of(creator,opponent));
             response.setData(dto);
             this.messagingTemplate.convertAndSend("/topic/" + room.getUuid().toString() + "/rematchState", dto);
         }
        }
        catch (Exception ex){
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }

        return response;
    }
    private boolean isUserAuthorized(User user, Rooms room, String role) {
        boolean isOwner = user.equals(room.getRoomOwner());
        boolean isOpponent = user.equals(room.getOpponent());

        if ((isOwner && !role.equals("ct")) || (isOpponent && !role.equals("op"))) {
            return false;
        } else if(!isOwner  && !isOpponent)
            return false;

        return true;
    }
    private boolean checkIfDraw(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (!(board[i][j] == 'X' || board[i][j] == 'O')) {
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    public boolean closeRoom(String uuid) {
        var room = roomRepository.findRoomsByUuid(UUID.fromString(uuid));
        if(room !=null)
            this.roomRepository.delete(room);

        this.messagingTemplate.convertAndSend("/topic/" + room.getUuid().toString() + "/closingRoom", true);
       return true;
    }

    @Override
    public ServiceResponse<rejoinRoomDTO> reconnectToRoom(Long roomId) throws JsonProcessingException {
        ServiceResponse<rejoinRoomDTO> response = new ServiceResponse<rejoinRoomDTO>();
        try{
            var room = roomRepository.findRoomsById(roomId);
            if(room !=null){
                var user = this.tokenValidationService.getUserFromToken();
                if(room.getRoomOwner().getId().equals(user.getId()))
                    response.setData(new rejoinRoomDTO("ct",room.getUuid().toString()));
                else {
                    assert room.getOpponent() != null;
                    if(room.getOpponent().getId().equals(user.getId()))
                        response.setData(new rejoinRoomDTO("op",room.getUuid().toString()));
                }
            }

        }catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    @Override
    public ServiceResponse<List<rankingDTO>> getLeaderboard() {
        ServiceResponse<List<rankingDTO>> response = new ServiceResponse<List<rankingDTO>>();
        try{
            List<User> users = userRepository.findAll();
            List<rankingDTO> rankingList = new ArrayList<>();

            for (User user : users) {
                int matches = user.getPlayedMatches();
                int wins = user.getWins();
                int loses = user.getLoses();
                int draws = user.getDraws();

                double winningPercentage = (wins + 0.5 * draws) / matches * 100.0;
                double roundedWinningPercentage = Math.round(winningPercentage * 100.0) / 100.0;


                rankingDTO ranking = new rankingDTO(user.getId(), user.getName(), matches, draws, loses, roundedWinningPercentage, wins);
                rankingList.add(ranking);
            }
            //Sort users by wins
            rankingList = rankingList.stream()
                    .sorted(Comparator.comparingInt(rankingDTO::getWins).reversed())
                    .collect(Collectors.toList());
            response.setData(rankingList);


        }catch (Exception ex){
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }
        return response;
    }
    @Scheduled(fixedRate = 60000)
    public void cleanupRooms() {
        List<Rooms> rooms = roomRepository.findAll();
        LocalDateTime currentTime = LocalDateTime.now();

        for (Rooms room : rooms) {
            LocalDateTime roomCreated = room.getCreatedDateTime();
            if (roomCreated != null) {
                Duration duration = Duration.between(roomCreated, currentTime);
                long minutesInactive = duration.toMinutes();

                if (minutesInactive >= 15 && minutesInactive <= 16) {
                    //Notify clients
                    this.messagingTemplate.convertAndSend("/topic/" + room.getUuid().toString() + "/timeOut", true);
                } else if(minutesInactive >= 17)
                    this.roomRepository.delete(room);


            }
        }
    }

    private String[][] convertCharBoardToStringBoard(char[][] charBoard) {
        int size = charBoard.length;
        String[][] stringBoard = new String[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                stringBoard[i][j] = String.valueOf(charBoard[i][j]);
            }
        }

        return stringBoard;
    }
    private long calculateTimeDifference(LocalDateTime time) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(time, currentTime);
        long secondsDifference = duration.toMillis() / 1000;
        // Check if the time difference is greater than 30 seconds
        if (secondsDifference > 30) {
            return 0;
        } else {
            return 30 - secondsDifference; // Return the remaining seconds
        }
    }

}
