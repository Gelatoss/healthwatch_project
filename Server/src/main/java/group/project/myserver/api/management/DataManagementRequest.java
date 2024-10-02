package group.project.myserver.api.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataManagementRequest {

    private Integer heartRate;
    private Integer steps;

}
