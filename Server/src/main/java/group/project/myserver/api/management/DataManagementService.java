package group.project.myserver.api.management;

import group.project.myserver.config.JwtService;
import group.project.myserver.data.Data;
import group.project.myserver.data.DataRepository;
import group.project.myserver.user.User;
import group.project.myserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DataManagementService {
    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public DataManagementResponse processData(String authHeader,DataManagementRequest request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed Authorization header");
        }
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Data data = Data.builder()
                .id_user(user.getId())
                .heart_rate(request.getHeartRate())
                .steps(request.getSteps())
                .time_stamp(LocalDateTime.now())
                .build();

        dataRepository.save(data);

        return DataManagementResponse.builder().build();
    }

}
