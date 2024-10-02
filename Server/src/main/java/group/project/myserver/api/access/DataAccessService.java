package group.project.myserver.api.access;

import group.project.myserver.config.JwtService;
import group.project.myserver.data.DataRepository;
import group.project.myserver.user.User;
import group.project.myserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DataAccessService {
    private final DataRepository dataRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public DataAccessResponse getDataDaily(String authHeader) {
        String username = validateAndGetUsername(authHeader);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        LocalDateTime startTime = LocalDateTime.now().minusHours(24);

        List<Object[]> rawData = dataRepository.dataLast24Hours(user.getId(), startTime);

        // Initialize a map with default values
        Map<Integer, DataType> hourlyDataMap = IntStream.range(0, 24)
                .boxed()
                .collect(Collectors.toMap(
                        hour -> hour,
                        hour -> new DataType(0.0, 0, startTime.withHour(hour).withMinute(0).withSecond(0).withNano(0))
                ));

        // Populate the map with actual data
        rawData.forEach(row -> {
            int hour = (int) row[0];
            double averageHeartRate = (double) row[1];
            long totalSteps = (long) row[2];
            LocalDateTime timestamp = startTime.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            hourlyDataMap.put(hour, new DataType(averageHeartRate, totalSteps, timestamp));
        });

        // Convert map values to a list
        List<DataType> hourlyData = hourlyDataMap.values().stream().sorted(Comparator.comparing(DataType::getTimeStamp)).collect(Collectors.toList());

        return DataAccessResponse.builder().data(hourlyData).build();
    }

    public DataAccessResponse getDataWeekly(String authHeader) {
        String username = validateAndGetUsername(authHeader);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        LocalDate startDate = LocalDate.now().minusDays(6);

        List<Object[]> rawData = dataRepository.averageDataPerDay(user.getId(), startDate.atStartOfDay());

        // Initialize a map with default values for the past 7 days
        Map<LocalDate, DataType> dailyDataMap = IntStream.range(0, 7)
                .boxed()
                .collect(Collectors.toMap(
                        day -> startDate.plusDays(day),
                        day -> new DataType(0.0, 0, startDate.plusDays(day).atStartOfDay())
                ));

        // Populate the map with actual data
        rawData.forEach(row -> {
            Date date = (Date) row[0];
            double averageHeartRate = (double) row[1];
            long totalSteps = (long) row[2];
            LocalDate localDate = date.toLocalDate();
            LocalDateTime timestamp = localDate.atStartOfDay();
            dailyDataMap.put(localDate, new DataType(averageHeartRate, totalSteps, timestamp));
        });

        // Convert map values to a list
        List<DataType> dailyData = dailyDataMap.values().stream()
                .sorted(Comparator.comparing(DataType::getTimeStamp))
                .collect(Collectors.toList());

        return DataAccessResponse.builder().data(dailyData).build();
    }

    public DataAccessResponse getDataMonthly(String authHeader) {
        String username = validateAndGetUsername(authHeader);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        LocalDate startDate = LocalDate.now().minusDays(29);

        List<Object[]> rawData = dataRepository.averageDataPerDay(user.getId(), startDate.atStartOfDay());

        // Initialize a map with default values for the past 30 days
        Map<LocalDate, DataType> dailyDataMap = IntStream.range(0, 30)
                .boxed()
                .collect(Collectors.toMap(
                        day -> startDate.plusDays(day),
                        day -> new DataType(0.0, 0, startDate.plusDays(day).atStartOfDay())
                ));

        // Populate the map with actual data
        rawData.forEach(row -> {
            java.sql.Date date = (java.sql.Date) row[0];
            double averageHeartRate = row[1] != null ? (double) row[1] : 0.0;
            long totalSteps = row[2] != null ? (long) row[2] : 0;
            LocalDate localDate = date.toLocalDate();
            LocalDateTime timestamp = localDate.atStartOfDay();
            dailyDataMap.put(localDate, new DataType(averageHeartRate, totalSteps, timestamp));
        });

        // Convert map values to a list
        List<DataType> dailyData = dailyDataMap.values().stream()
                .sorted(Comparator.comparing(DataType::getTimeStamp))
                .collect(Collectors.toList());

        return DataAccessResponse.builder().data(dailyData).build();
    }


    private String validateAndGetUsername(String authHeader) {
        if (authHeader == null || ! authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed Authorization header");
        }
        return jwtService.extractUsername(authHeader.substring(7));
    }
}
