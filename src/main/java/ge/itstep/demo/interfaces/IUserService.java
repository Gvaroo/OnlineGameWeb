package ge.itstep.demo.interfaces;

import ge.itstep.demo.dto.LoginUserDTO;
import ge.itstep.demo.dto.RegisterUserDTO;
import ge.itstep.demo.dto.UserInfoDTO;
import ge.itstep.demo.model.ServiceResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<ServiceResponse<Long>> register(RegisterUserDTO newUser);
    CompletableFuture<ServiceResponse<UserInfoDTO>> login(LoginUserDTO credentials);
    ServiceResponse<Boolean> isLoggedIn();
    CompletableFuture<Boolean> userExists(String email);

    Boolean logout();
}
