package group.project.myserver.api.management;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DataManagementController {

    private final DataManagementService service;

    @PostMapping("/data_management")
    public ResponseEntity<DataManagementResponse> processData(
            @RequestHeader("Authorization") String token,
            @RequestBody DataManagementRequest request
    ) {
        DataManagementResponse response = service.processData(token, request);
        return ResponseEntity.ok(response);
    }

}
