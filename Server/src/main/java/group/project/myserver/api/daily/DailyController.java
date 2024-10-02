package group.project.myserver.api.daily;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DailyController {
    private final  DailyService service;

    @GetMapping("/daily_report")
    public ResponseEntity<DailyResponse> dailyReport(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(service.dailyReport(token));
    }
}
