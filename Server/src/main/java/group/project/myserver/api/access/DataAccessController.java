package group.project.myserver.api.access;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/data_access")
@RequiredArgsConstructor
public class DataAccessController {

    private final DataAccessService service;

    @GetMapping("/daily")
    public ResponseEntity<DataAccessResponse> getDailyData(
            @RequestHeader("Authorization") String request
    ) {
        return ResponseEntity.ok(service.getDataDaily(request));
    }

    @GetMapping("/weekly")
    public ResponseEntity<DataAccessResponse> getWeeklyData(
            @RequestHeader("Authorization") String request
    ) {
        return ResponseEntity.ok(service.getDataWeekly(request));
    }

    @GetMapping("/monthly")
    public ResponseEntity<DataAccessResponse> getMonthlyData(
            @RequestHeader("Authorization") String request
    ) {
        return ResponseEntity.ok(service.getDataMonthly(request));
    }
}

