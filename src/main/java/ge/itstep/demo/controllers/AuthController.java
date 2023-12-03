package ge.itstep.demo.controllers;

import ge.itstep.demo.dto.LoginUserDTO;
import ge.itstep.demo.dto.RegisterUserDTO;
import ge.itstep.demo.dto.UserInfoDTO;
import ge.itstep.demo.dto.game.rankingDTO;
import ge.itstep.demo.interfaces.IGameService;
import ge.itstep.demo.interfaces.IUserService;
import ge.itstep.demo.model.ServiceResponse;
import ge.itstep.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/public")
public class AuthController {



    @Autowired
    private final IUserService userService;
    @Autowired
    private final IGameService gameService;


    @Autowired
    public AuthController(UserService userService, IGameService gameService) {

        this.userService = userService;


        this.gameService = gameService;
    }

    @PostMapping("/register")
    public ResponseEntity<ServiceResponse<Long>> register(@RequestBody RegisterUserDTO newUser) {
        try {
            CompletableFuture<ServiceResponse<Long>> futureResult = userService.register(newUser);
            ServiceResponse<Long> result = futureResult.get();
            return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/getLeaderBoard")
    public ResponseEntity<ServiceResponse<List<rankingDTO>>> getLeaderBoard() {
        ServiceResponse<List<rankingDTO>> result = gameService.getLeaderboard();
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<UserInfoDTO>> login(@RequestBody LoginUserDTO credentials) {
        try {
            CompletableFuture<ServiceResponse<UserInfoDTO>> futureResult = userService.login(credentials);
            ServiceResponse<UserInfoDTO> result = futureResult.get();
            return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
        } catch (InterruptedException | ExecutionException e) {
            // Handle the exception accordingly
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/IsLoggedIn")
    public ResponseEntity<ServiceResponse<Boolean>> isLoggedIn() {
        ServiceResponse<Boolean> result = userService.isLoggedIn();
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
    @GetMapping("/LogOut")
    public Boolean logOut(){
        return userService.logout();
    }

}
