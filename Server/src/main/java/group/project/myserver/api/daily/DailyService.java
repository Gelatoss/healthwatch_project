package group.project.myserver.api.daily;

import group.project.myserver.config.JwtService;
import group.project.myserver.data.DataRepository;
import group.project.myserver.user.User;
import group.project.myserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyService {
    private final DataRepository dataRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public DailyResponse dailyReport(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed Authorization header");
        }
        String username = jwtService.extractUsername(authHeader.substring(7));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Object[]> results = dataRepository.todayData(user.getId());
        if (!results.isEmpty()) {
            Object[] row = results.get(0); // Assuming there's only one result
            Integer averageHeartRate = (Integer) row[0];
            Long totalSteps = (Long) row[1]; // Use Long for sum as it might exceed Integer range

            return DailyResponse.builder()
                    .averageBPM(averageHeartRate != null ? averageHeartRate : 0)
                    .totalSteps(totalSteps != null ? totalSteps : 0)
                    .build();
        } else {
            return DailyResponse.builder()
                    .averageBPM(0)
                    .totalSteps(0L)
                    .build();
        }
    }
}
