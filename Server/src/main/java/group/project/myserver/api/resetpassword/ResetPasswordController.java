package group.project.myserver.api.resetpassword;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ResetPasswordController {
    private final ResetPasswordService service;

    @PostMapping("/reset")
    public ResponseEntity<ResetPasswordResponse> reset(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ResetPasswordRequest request
    ) {
        return ResponseEntity.ok(service.resetPassword(authHeader, request));
    }
}
