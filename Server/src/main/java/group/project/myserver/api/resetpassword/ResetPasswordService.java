package group.project.myserver.api.resetpassword;

import group.project.myserver.config.JwtService;
import group.project.myserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordResponse resetPassword(String authHeader, ResetPasswordRequest request) {
        try {
            String username = validateAndGetUsername(authHeader);
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResetPasswordResponse.builder().message("Password changed").build();
        } catch (ResponseStatusException e) {
            throw e; // Re-throwing to handle it in the controller
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while resetting the password", e);
        }
    }

    private String validateAndGetUsername(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed Authorization header");
        }
        return jwtService.extractUsername(authHeader.substring(7));
    }
}
