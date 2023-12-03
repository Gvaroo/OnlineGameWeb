package ge.itstep.demo.filter;

import ge.itstep.demo.service.TokenValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenValidationService tokenValidationService;


    public JwtAuthenticationFilter(TokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;

    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenValidationService.extractTokenFromCookie(request);
        if (token != null) {
            try {
                SecurityContextHolder.getContext().setAuthentication(
                        tokenValidationService.validateToken(token));
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                throw e;
            }


    }
        filterChain.doFilter(request, response);
}


}
