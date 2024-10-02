package group.project.myserver.api.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataType {
    private double averageHeartRate;
    private long totalSteps;
    private LocalDateTime timeStamp;
}
