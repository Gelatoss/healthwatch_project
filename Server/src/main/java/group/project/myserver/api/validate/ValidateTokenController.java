package group.project.myserver.api.validate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ValidateTokenController {

    private final ValidateTokenService service;

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(
            @RequestBody ValidateTokenRequest request
    ) {
        return ResponseEntity.ok(service.checkValidation(request.getToken()));
    }

}
