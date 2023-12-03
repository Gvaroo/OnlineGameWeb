package ge.itstep.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ge.itstep.demo.dto.LoginUserDTO;
import ge.itstep.demo.dto.RegisterUserDTO;
import ge.itstep.demo.dto.UserInfoDTO;
import ge.itstep.demo.interfaces.IUserService;
import ge.itstep.demo.model.Role;
import ge.itstep.demo.model.ServiceResponse;
import ge.itstep.demo.model.User;
import ge.itstep.demo.repository.RoleRepository;
import ge.itstep.demo.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Jwts;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService implements IUserService {
    @Autowired
    private RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenValidationService tokenValidationService;
    private final jakarta.servlet.http.HttpServletResponse httpServletResponse;
    private static final String SECRET_KEY = "W9mFHowKsCIbYxPfT7ajoeKbniWb4lBsxOo+F+muqicL2tr/QssiCRhSTzhXaMjsHukkb7C/Y7lVjOXAAZe9Eg==";



    public UserService(UserRepository userRepository, TokenValidationService tokenValidationService, HttpServletResponse httpServletResponse) {
        this.userRepository = userRepository;
        this.tokenValidationService = tokenValidationService;

        this.httpServletResponse = httpServletResponse;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public CompletableFuture<ServiceResponse<Long>> register(RegisterUserDTO newUser) {
        CompletableFuture<ServiceResponse<Long>> futureResponse = new CompletableFuture<>();
        ServiceResponse<Long> response = new ServiceResponse<>();

        try {

            // Check if the user with the given email already exists
            if (userRepository.existsByEmail(newUser.getEmail())) {
                response.setSuccess(false);
                response.setMessage("User with that email is already registered!");
                futureResponse.complete(response);
                return futureResponse;
            }
            Role role = roleRepository
                    .findRoleByName("ROLE_USER");
            if (role == null) {
                role = roleRepository.save(new Role("ROLE_USER"));
            }
            String hashedPassword = passwordEncoder.encode(newUser.getPassword());
            User user = new User(newUser.getFullName(), newUser.getEmail(),hashedPassword);
            user.setRoles(Arrays.asList(role));

            userRepository.save(user);
            response.setData(user.getId());
            futureResponse.complete(response);
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
            futureResponse.complete(response);

        }
        return futureResponse;
    }

    @Override
    public CompletableFuture<ServiceResponse<UserInfoDTO>> login(LoginUserDTO credentials) {
        CompletableFuture<ServiceResponse<UserInfoDTO>> futureResponse = new CompletableFuture<>();
        ServiceResponse<UserInfoDTO> response = new ServiceResponse<>();


        try {
            User user = userRepository.findUserByEmail(credentials.getEmail());
            if (user == null) {
                response.setSuccess(false);
                response.setMessage("User not found");
                futureResponse.complete(response);
            } else if (user.getPasswordHash() == null) {
                response.setSuccess(false);
                response.setMessage("You tried signing in with a different authentication method than the one you used during signup. Please try again using your original authentication method");
                futureResponse.complete(response);
            } else if (!verifyPasswordHash(credentials.getPassword(), user.getPasswordHash())) {
                response.setSuccess(false);
                response.setMessage("Password is incorrect!");
                futureResponse.complete(response);
            } else {
                UserInfoDTO userInfo = new UserInfoDTO();
                userInfo.setEmail(user.getEmail());
                userInfo.setName(user.getName());




                // Generate token
                String token = generateToken(user);
                response.setData(userInfo);
                setCookie(token,httpServletResponse);
                futureResponse.complete(response);
            }
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }

        return futureResponse;
    }

    @Override
    public ServiceResponse<Boolean> isLoggedIn() {
        ServiceResponse<Boolean> response = new ServiceResponse<Boolean>();
        try{
            var user = this.tokenValidationService.getUserFromToken();
            if(user !=null)
                response.setData(true);
            else
                response.setData(false);
        }catch (Exception ex){
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
        }
        return response;
    }



    @Override
    public CompletableFuture<Boolean> userExists(String email) {
        return null;
    }

    private boolean verifyPasswordHash(String password, String passwordHash) {
        return passwordEncoder.matches(password, passwordHash);
    }

    private String generateToken(User user) throws JsonProcessingException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86400000);


        ObjectMapper objectMapper = new ObjectMapper();

        try {

            String claimsJson = objectMapper.writeValueAsString(user);

            String token = Jwts.builder()
                    .setSubject(Long.toString(user.getId()))
                    .claim("user", claimsJson)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS512)
                    .compact();

            return token;
        } catch (JsonProcessingException e) {

            e.printStackTrace();
            return null;
        }
    }



    private void setCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

    }
    @Override
    public Boolean logout() {
        this.removeCookie("access_token", httpServletResponse);
        return true;
    }
    private void removeCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(0); // Setting maxAge to 0 removes the cookie
        response.addCookie(cookie);
    }
}
