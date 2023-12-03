package ge.itstep.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.itstep.demo.model.User;
import ge.itstep.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Collections;

@Service
public class TokenValidationService {
    private final SecretKey secretKey;
    private final jakarta.servlet.http.HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;


    public TokenValidationService(HttpServletRequest httpServletRequest, UserRepository userRepository) {
        this.httpServletRequest = httpServletRequest;
        this.userRepository = userRepository;

        this.secretKey = Keys.hmacShaKeyFor("W9mFHowKsCIbYxPfT7ajoeKbniWb4lBsxOo+F+muqicL2tr/QssiCRhSTzhXaMjsHukkb7C/Y7lVjOXAAZe9Eg==".getBytes());
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;

    }
    public User getUserFromToken() throws JsonProcessingException {
       String token = extractTokenFromCookie(httpServletRequest);
       if(token != null) {
           Jws<Claims> claimsJws = Jwts.parser()
                   .setSigningKey(secretKey)
                   .parseClaimsJws(token);

           Claims body = claimsJws.getBody();

           // Deserialize user claims from JSON using Jackson
           String userClaimsJson = body.get("user", String.class);
           ObjectMapper objectMapper = new ObjectMapper();
           User userExtracted = objectMapper.readValue(userClaimsJson, User.class);
           User user = userRepository.findUserById(userExtracted.getId());
           return user;
       }
       return null;
    }

    public Authentication validateToken(String token) {
        if (StringUtils.hasText(token)) {
            try {
                Jws<Claims> claimsJws = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token);

                Claims body = claimsJws.getBody();

                // Deserialize user claims from JSON using Jackson
                String userClaimsJson = body.get("user", String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(userClaimsJson, User.class);


                Authentication authentication = new UsernamePasswordAuthenticationToken(user,null, Collections.emptyList());


                return authentication;
            } catch (Exception e) {
                // Token is not valid
                return null;
            }
        }
        return null;
    }


}
