package group.project.myserver.api.userinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserExtraResponse {
    private Integer age;
    private Integer height;
    private Float weight;
    private String country;
    private String city;
    private String street;
    private String phone;
}
