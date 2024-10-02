package group.project.myserver.api.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyResponse {
    private Integer averageBPM;
    private Long totalSteps;
}