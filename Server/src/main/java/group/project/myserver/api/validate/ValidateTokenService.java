package group.project.myserver.api.validate;

import group.project.myserver.config.JwtService;
import group.project.myserver.token.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateTokenService {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    public Boolean checkValidation(String token) {
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Boolean isExpired = jwtService.isTokenValid(token, userDetails);
            Boolean isRevoked = tokenRepository.tokenIsRevoked(token).orElse(false);
            return isExpired && !isRevoked;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed", e);
        }
    }
}
