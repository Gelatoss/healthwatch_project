package group.project.myserver.api.userinfo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserExtraController {

    private final UserExtraService service;

    @PostMapping("/extra/add")
    public ResponseEntity<UserExtraResponseCode> addExtra(
            @RequestHeader("Authorization") String token,
            @RequestBody UserExtraRequest request
    ) {
        return ResponseEntity.ok(service.createOrUpdateUserExtra(token, request));
    }

    @GetMapping("/extra/get")
    public ResponseEntity<UserExtraResponse> getExtra(
    @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(service.replyUserExtra(token));
    }

    @GetMapping("/extra/info")
    public ResponseEntity<UserInfoResponse> getInfo(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(service.userInfo(token));
    }
}
